package me.choicore.samples.meter.domain

interface Measurer {
    fun measure(measurand: Measurand): Metric?
}
