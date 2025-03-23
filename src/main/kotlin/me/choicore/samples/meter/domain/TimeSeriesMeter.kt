package me.choicore.samples.meter.domain

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS
import java.util.Collections

@Service
class TimeSeriesMeter(
    private val meteringStrategyResolver: MeteringStrategyResolver,
) : Meter {
    fun measure(
        lotId: Long,
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
                        lotId = lotId,
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = startDateTimeInclusive.toLocalTime(),
                    )
            }

            1L -> {
                metrics +=
                    this.measure(
                        lotId = lotId,
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                metrics +=
                    this.measure(
                        lotId = lotId,
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }

            else -> {
                metrics +=
                    this.measure(
                        lotId = lotId,
                        measureOn = start,
                        startTimeInclusive = startDateTimeInclusive.toLocalTime(),
                        endTimeExclusive = LocalTime.MIDNIGHT,
                    )
                (1 until between)
                    .forEach {
                        metrics +=
                            this.measure(
                                lotId = lotId,
                                measureOn = start.plusDays(it),
                                startTimeInclusive = LocalTime.MIDNIGHT,
                                endTimeExclusive = LocalTime.MIDNIGHT,
                            )
                    }
                metrics +=
                    this.measure(
                        lotId = lotId,
                        measureOn = end,
                        startTimeInclusive = LocalTime.MIDNIGHT,
                        endTimeExclusive = endDateTimeExclusive.toLocalTime(),
                    )
            }
        }

        return Collections.unmodifiableList(metrics)
    }

    override fun measure(measurand: Measurand): List<Metric> = meteringStrategyResolver.resolve(measurand).measure(measurand)
}
