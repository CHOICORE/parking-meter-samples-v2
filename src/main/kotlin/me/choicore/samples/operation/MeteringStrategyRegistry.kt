package me.choicore.samples.operation

import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class MeteringStrategyRegistry(
    private val dayOfWeekStrategies: List<MeteringDayOfWeekStrategy>,
    private val specificDateStrategies: List<MeteringSpecificDateStrategy>,
) : TimeBasedMeteringStrategy {
    private val daysOfWeek =
        dayOfWeekStrategies
            .groupBy { it.dayOfWeek }
            .mapValues { (_, strategies: List<MeteringDayOfWeekStrategy>) ->
                strategies.sortedByDescending { it.effectiveDate }
            }

    private val specifies: Map<LocalDate, MeteringSpecificDateStrategy> =
        specificDateStrategies.associateBy { it.specifiedDate }

    private val cache = ConcurrentHashMap<LocalDate, TimeBasedMeteringStrategy?>()

    override fun applies(measuredOn: LocalDate): Boolean = cache[measuredOn] != null

    fun getMeteringStrategy(measuredOn: LocalDate): TimeBasedMeteringStrategy {
        return cache.computeIfAbsent(measuredOn) { date ->
            specifies[date]?.let<MeteringSpecificDateStrategy, Nothing> {
                return@computeIfAbsent it
            }

            val applicableStrategies = daysOfWeek[date.dayOfWeek]
            if (applicableStrategies != null) {
                for (strategy in applicableStrategies) {
                    if (strategy.applies(date)) {
                        return@computeIfAbsent strategy
                    }
                }
            }
            null
        } ?: throw NoSuchElementException("No metering strategy found for $measuredOn")
    }
}
