package me.choicore.samples.meter

interface Measurer {
    fun measure(measurand: Measurand): Metric?
}
