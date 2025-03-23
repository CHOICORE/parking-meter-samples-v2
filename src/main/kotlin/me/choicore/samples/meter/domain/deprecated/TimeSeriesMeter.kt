package me.choicore.samples.meter.domain.deprecated

import me.choicore.samples.meter.domain.Measurand
import me.choicore.samples.meter.domain.Meter
import me.choicore.samples.meter.domain.Metric
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS
import java.util.Collections

@Deprecated("unused")
class TimeSeriesMeter(
    private val registry: TimeBasedMeteringStrategyRegistry,
) : Meter {
    fun measure(
        startDateTimeInclusive: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
    ): List<Metric> {
        val start: LocalDate = startDateTimeInclusive.toLocalDate()
        val end: LocalDate = endDateTimeExclusive.toLocalDate()

        val metrics: MutableList<Metric> = mutableListOf()
        when (val between: Long = DAYS.between(start, end)) {
            0L -> {
                metrics +=
                    this.measure(
                        Measurand(
                            measureOn = start,
                            from = startDateTimeInclusive.toLocalTime(),
                            to = endDateTimeExclusive.toLocalTime(),
                        ),
                    )
            }

            1L -> {
                metrics +=
                    this.measure(
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                metrics +=
                    this.measure(
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }

            else -> {
                metrics +=
                    this.measure(
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                (1 until between)
                    .forEach {
                        metrics +=
                            this.measure(
                                measureOn = start.plusDays(it),
                                startTimeInclusive = LocalTime.MIDNIGHT,
                                endTimeExclusive = LocalTime.MIDNIGHT,
                            )
                    }
                metrics +=
                    this.measure(
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }
        }

        return Collections.unmodifiableList(metrics)
    }

    override fun measure(measurand: Measurand): List<Metric> = registry.measure(measurand)
}
