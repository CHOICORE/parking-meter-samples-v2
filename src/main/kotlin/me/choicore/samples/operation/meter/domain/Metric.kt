package me.choicore.samples.operation.meter.domain

import java.time.Duration

data class Metric(
    val measurand: Measurand,
    val measurer: Measurer,
    val usage: Duration,
)
