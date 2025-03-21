package me.choicore.samples.meter

import java.time.LocalDate
import java.time.LocalTime

interface Meter {
    fun measure(measurand: Measurand): List<Metric>

    fun measure(
        measureOn: LocalDate,
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): List<Metric> =
        this.measure(
            Measurand(
                measureOn = measureOn,
                from = startTimeInclusive,
                to = endTimeExclusive,
            ),
        )
}
