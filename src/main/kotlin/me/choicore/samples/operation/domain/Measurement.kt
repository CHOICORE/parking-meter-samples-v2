package me.choicore.samples.operation.domain

import java.time.Duration

data class Measurement(
    val measurand: Measurand,
    val measurer: Measurer,
    val measurement: Duration,
    val factor: Double = 1.0,
)
