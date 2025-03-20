package me.choicore.samples.operation.meter.domain

interface Measurer {
    fun measure(measurand: Measurand): Metric?
}
