package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

class CityGroupOrMarketGroup(val origin: Group<*, Network>, val isMarketGroup: Boolean) {

    constructor(marketGroup: Group<Market, Network>) : this(origin = marketGroup, isMarketGroup = true)

    constructor(cityGroup: Group<City, Network>) : this(origin = cityGroup, isMarketGroup = false)

    val marketGroup: Group<Market, Network>? get() = if (isMarketGroup) origin as Group<Market, Network> else null

    val cityGroup: Group<City, Network>? get() = if (!isMarketGroup) origin as Group<City, Network> else null

    val isCityGroup: Boolean get() = !isMarketGroup
}