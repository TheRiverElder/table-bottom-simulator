package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card


val CARD_SET_BY_PLAYER_AMOUNT_CITIES = listOf(
    // 青色
    "belper" to listOf(0, 0, 2),
    "derby" to listOf(0, 0, 3),
    // 蓝色
    "leek" to listOf(0, 2, 2),
    "stoke_on_trent" to listOf(0, 3, 3),
    "stone" to listOf(0, 2, 2),
    "uttoxeter" to listOf(0, 1, 2),
    // 红色
    "stafford" to listOf(2, 2, 2),
    "burton_on_trent" to listOf(2, 2, 2),
    "cannock" to listOf(2, 2, 2),
    "tamworth" to listOf(1, 1, 1),
    "walsall" to listOf(1, 1, 1),
    // 黄色
    "coalbrookdale" to listOf(3, 3, 3),
    "dudley" to listOf(2, 2, 2),
    "kidderminster" to listOf(2, 2, 2),
    "wolverhampton" to listOf(2, 2, 2),
    "worcester" to listOf(2, 2, 2),
    // 紫色
    "birmingham" to listOf(3, 3, 3),
    "coventry" to listOf(3, 3, 3),
    "nuneaton" to listOf(1, 1, 1),
    "redditch" to listOf(1, 1, 1),
)

val CARD_SET_BY_PLAYER_AMOUNT_FACTORY_TYPES = listOf(
    // 产业牌
    "iron_works" to listOf(4, 4, 4),
    "coal_mine" to listOf(2, 2, 3),
    "cotton_mill_or_manufacturer" to listOf(0, 6, 8),
    "pottery" to listOf(2, 2, 3),
    "brewery" to listOf(5, 5, 5),
)

val CARD_SET_BY_PLAYER_AMOUNT = CARD_SET_BY_PLAYER_AMOUNT_CITIES + CARD_SET_BY_PLAYER_AMOUNT_FACTORY_TYPES

val ROUNDS_OF_EACH_ERA_BY_PLAYER_AMOUNT = listOf(10, 9, 8)

val FACTORY_SET = listOf(
    "brewery" to listOf(2, 2, 2, 1),
    "manufacturer" to listOf(1, 2, 1, 1, 2, 1, 1, 2),
    "cotton_mill" to listOf(3, 2, 3, 3),
    "pottery" to listOf(1, 1, 1, 1, 1),
    "iron_works" to listOf(1, 1, 1, 1),
    "coal_mine" to listOf(1, 2, 2, 2),
)

val SELLABLE_FACTORY_TYPE_LIST = listOf(
    "manufacturer",
    "cotton_mill",
    "pottery",
)

// 返回null：这张卡不是产业牌
// 返回size = 0的List：这是一张任意类型产业牌
// 返回size > 0的List：内容为该产业牌可以应用的产业类型，一般只有一个，但是有的牌有多个
val Card.factoryTypeNames: List<String>?
    get() {
        if (series.name != "birmingham:card") return null
        return when (name) {
            "iron_works" -> listOf(name)
            "coal_mine" -> listOf(name)
            "cotton_mill_or_manufacturer" -> listOf("cotton_mill", "manufacturer")
            "pottery" -> listOf(name)
            "brewery" -> listOf(name)
            "any" -> emptyList()
            else -> null
        }
    }

// 返回null：这张卡不是城市牌
// 返回""：这是一张任意类型城市牌
// 返回非空String：内容为该城市牌可以应用的城市，一般只有一个
val Card.cityName: String?
    get() {
        if (series.name != "birmingham:card") return null
        return when (name) {
            in CARD_SET_BY_PLAYER_AMOUNT_CITIES.map { it.first } -> name
            "wild" -> ""
            else -> null
        }
    }