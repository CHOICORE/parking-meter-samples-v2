package me.choicore.samples.operation

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS

class Meter(
    private val registry: MeteringStrategyRegistry,
) {
    fun measure(
        startDateTimeInclusive: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
    ): List<Measurement> {
        val start = startDateTimeInclusive.toLocalDate()
        val end = endDateTimeExclusive.toLocalDate()

        val measurements: MutableList<Measurement> = mutableListOf()
        when (val between: Long = DAYS.between(start, end)) {
            0L -> {
                measurements +=
                    registry.measure(
                        Measurand(
                            date = start,
                            from = startDateTimeInclusive.toLocalTime(),
                            to = endDateTimeExclusive.toLocalTime(),
                        ),
                    )
            }

            1L -> {
                measurements += registry.measure(start, startDateTimeInclusive.toLocalTime(), LocalTime.MIDNIGHT)
                measurements += registry.measure(end, LocalTime.MIDNIGHT, endDateTimeExclusive.toLocalTime())
            }

            else -> {
                measurements +=
                    registry.measure(
                        start,
                        startDateTimeInclusive.toLocalTime(),
                        endDateTimeExclusive.toLocalTime(),
                    )
                (1 until between)
                    .forEach {
                        measurements += registry.measure(start.plusDays(it), LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
                    }
                measurements += registry.measure(end, LocalTime.MIDNIGHT, endDateTimeExclusive.toLocalTime())
            }
        }

        TODO()
    }
}
