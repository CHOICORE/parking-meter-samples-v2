package me.choicore.samples.operation.domain

data class Measurement(
    val measurand: Measurand,
    val measurer: Measurer,
    val measurement: Long,
    val factor: Double = 1.0,
)
