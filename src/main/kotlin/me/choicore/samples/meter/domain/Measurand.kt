package me.choicore.samples.meter.domain

import java.time.LocalDate
import java.time.LocalTime

data class Measurand(
    val measureOn: LocalDate,
    val from: LocalTime,
    val to: LocalTime,
)
