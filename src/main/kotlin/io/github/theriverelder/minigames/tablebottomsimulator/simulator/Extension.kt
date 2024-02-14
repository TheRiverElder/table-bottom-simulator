package io.github.theriverelder.minigames.tablebottomsimulator.simulator

import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable

interface Extension : Persistable {
    val name: String

    // 创建新的Simulator时被调用，同一个存档只会调用一次
    fun initialize()


    // 载入旧的Simulator时被调用 Persistable.restore()
}