package me.choicore.samples.meter.domain

interface Measurer {
    val calibration: Calibration

    fun measure(measurand: Measurand): Metric?
}
