package me.choicore.samples.operation

import java.time.LocalDate
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
        val start: LocalDate = startDateTimeInclusive.toLocalDate()
        val end: LocalDate = endDateTimeExclusive.toLocalDate()

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
                measurements +=
                    measure(
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                measurements +=
                    measure(
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

    fun measure(measurand: Measurand): List<Measurement> = registry.measure(measurand)

    private fun measure(
        measureOn: LocalDate,
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): List<Measurement> =
        this.measure(
            Measurand(
                date = measureOn,
                from = startTimeInclusive,
                to = endTimeExclusive,
            ),
        )
}
