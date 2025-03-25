package me.choicore.samples.meter.domain

interface MeteringStrategySelector {
    fun select(measurand: Measurand): MeteringStrategy
}
