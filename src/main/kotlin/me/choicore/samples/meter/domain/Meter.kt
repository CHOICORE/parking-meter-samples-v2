package me.choicore.samples.meter.domain

import java.time.LocalDate
import java.time.LocalTime

interface Meter {
    fun measure(measurand: Measurand): List<Metric>

    fun measure(
        lotId: Long,
        measureOn: LocalDate,
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): List<Metric> =
        this.measure(
            Measurand(
                lotId = lotId,
                measureOn = measureOn,
                from = startTimeInclusive,
                to = endTimeExclusive,
            ),
        )
}
