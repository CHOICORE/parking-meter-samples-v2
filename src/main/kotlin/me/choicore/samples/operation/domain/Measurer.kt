package me.choicore.samples.operation.domain

interface Measurer {
    fun measure(measurand: Measurand): Measurement
}
