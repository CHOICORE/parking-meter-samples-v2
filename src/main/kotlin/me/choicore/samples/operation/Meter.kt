package me.choicore.samples.operation

import java.time.LocalDateTime
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
                val meteringStrategy = registry.getMeteringStrategy(start)
            }

            1L -> {
            }

            else -> {
                (1 until between)
                    .forEach {
                    }
            }
        }

        TODO()
    }
}
