package me.choicore.samples.meter

import java.time.Duration

data class Metric(
    val measurand: Measurand,
    val measurer: Measurer,
    val usage: Duration,
)
