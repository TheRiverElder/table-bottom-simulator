package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.AABBArea
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.addAll
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PileBehavior.PileEvent
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import kotlinx.serialization.json.*

class ControllerBehavior(type: BehaviorType<ControllerBehavior>, host: GameObject, uid: Int) : BehaviorAdaptor<ControllerBehavior>(type, host, uid) {

    val onDragStartListeners = ListenerManager<PointerEvent>()
    val onDragMoveListeners = ListenerManager<PointerEvent>()
    val onDragEndListeners = ListenerManager<PointerEvent>()
    val onClickListeners = ListenerManager<PointerEvent>()

    var draggable: Boolean = true
    var controller: User? = null

    override fun onInitialize() {
        onDragStartListeners.add(onDragStart)
        onDragMoveListeners.add(onDragMove)
        onDragEndListeners.add(onDragEnd)
    }

    private val onDragStart = { event: PointerEvent ->
        this.controller = event.controller
        sendUpdate()
    }

    private val onDragMove = { event: PointerEvent ->
        host.position = event.position
        host.sendUpdateSelf()
    }

    private val onDragEnd = { _: PointerEvent ->
        this.controller = null
        sendUpdate()
        val pile = simulator.gameObjects.values.find { host.position in AABBArea(it.position, it.size) }
        if (pile != null) {
            val pileBehavior = pile.getBehaviorByType(PileBehavior.TYPE)
            pileBehavior?.onPileListeners?.emit(PileEvent(pile, host))
        }
    }

    override fun receiveInstruction(data: JsonObject, sender: User) {
        val eventType = data.forceGet("eventType").jsonPrimitive.content
        val position = restoreVector2(data.forceGet("position").jsonObject)
        val event = PointerEvent(sender, position)
        when (eventType) {
            EVENT_DRAG_START -> onDragStartListeners.emit(event)
            EVENT_DRAG_MOVE -> onDragMoveListeners.emit(event)
            EVENT_DRAG_END -> onDragEndListeners.emit(event)
        }

    }

    override fun save(): JsonObject = buildJsonObject {
        addAll(super.save())
        put("draggable", JsonPrimitive(draggable))
        put("controller", JsonPrimitive(controller?.uid))
    }

    override fun restore(data: JsonObject) {
        super.restore(data)
        this.draggable = data["draggable"]?.jsonPrimitive?.booleanOrNull ?: false
        val controllerUid = data["controller"]?.jsonPrimitive?.intOrNull
        if (controllerUid != null) {
            val controller = simulator.users[controllerUid]
            if (controller != null) {
                this.controller = controller
            }
        }
    }

    companion object {
        val TYPE = BehaviorType("controller", Side.BOTH, ::ControllerBehavior)

        const val EVENT_DRAG_START = "drag_start"
        const val EVENT_DRAG_MOVE = "drag_move"
        const val EVENT_DRAG_END = "drag_end"
    }

    class PointerEvent(
        val controller: User,
        val position: Vector2,
    )
}