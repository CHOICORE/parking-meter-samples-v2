package me.choicore.samples.operation

import java.time.LocalDate
import java.time.LocalTime

data class Measurand(
    val date: LocalDate,
    val from: LocalTime,
    val to: LocalTime,
)
