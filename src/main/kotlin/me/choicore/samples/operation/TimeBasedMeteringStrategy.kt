package me.choicore.samples.operation

import java.time.LocalDate
import java.time.LocalTime

interface TimeBasedMeteringStrategy {
    fun applies(measuredOn: LocalDate): Boolean

    fun measure(measurand: Measurand): List<Measurement>

    fun measure(
        date: LocalDate,
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): List<Measurement> = this.measure(Measurand(date = date, from = startTimeInclusive, to = endTimeExclusive))
}
