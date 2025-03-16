package me.choicore.samples.operation

import java.time.Duration

data class Measurement(
    val measurand: Measurand,
    val measurer: Measurer,
    val measurement: Duration,
)
