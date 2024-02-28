package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

class CityGroupOrMarketGroup(val origin: Group<*, Network>, val isMarketGroup: Boolean) {

    val marketGroup: Group<Market, Network>? get() = if (isMarketGroup) origin as Group<Market, Network> else null

    val cityGroup: Group<City, Network>? get() = if (!isMarketGroup) origin as Group<City, Network> else null

    val isCityGroup: Boolean get() = !isMarketGroup
}

fun wrapMarketGroup(marketGroup: Group<Market, Network>): CityGroupOrMarketGroup =
    CityGroupOrMarketGroup(marketGroup, true)


fun wrapCityGroup(cityGroup: Group<City, Network>): CityGroupOrMarketGroup =
    CityGroupOrMarketGroup(cityGroup, false)