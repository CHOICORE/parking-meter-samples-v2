package me.choicore.samples.operation.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS

class TimeSeriesMeter(
    private val registry: MeteringStrategyRegistry,
) : Meter {
    fun measure(
        startDateTimeInclusive: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
    ): List<Measurement> {
        val start: LocalDate = startDateTimeInclusive.toLocalDate()
        val end: LocalDate = endDateTimeExclusive.toLocalDate()

        val measurements: MutableList<Measurement> = mutableListOf()
        when (val between: Long = DAYS.between(start, end)) {
            0L -> {
                measurements +=
                    this.measure(
                        Measurand(
                            measureOn = start,
                            from = startDateTimeInclusive.toLocalTime(),
                            to = endDateTimeExclusive.toLocalTime(),
                        ),
                    )
            }

            1L -> {
                measurements +=
                    this.measure(
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                measurements +=
                    this.measure(
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }

            else -> {
                measurements +=
                    this.measure(
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
                (1 until between)
                    .forEach {
                        measurements +=
                            this.measure(
                                measureOn = start.plusDays(it),
                                startTimeInclusive = LocalTime.MIDNIGHT,
                                endTimeExclusive = LocalTime.MIDNIGHT,
                            )
                    }
                measurements +=
                    this.measure(
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }
        }

        return measurements.toList()
    }

    override fun measure(measurand: Measurand): List<Measurement> = registry.measure(measurand)
}
