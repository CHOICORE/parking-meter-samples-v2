package me.choicore.samples.operation.domain

import java.time.LocalDate
import java.time.LocalTime

interface Meter {
    fun measure(measurand: Measurand): List<Measurement>

    fun measure(
        measureOn: LocalDate,
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): List<Measurement> = this.measure(Measurand(measureOn = measureOn, from = startTimeInclusive, to = endTimeExclusive))
}
