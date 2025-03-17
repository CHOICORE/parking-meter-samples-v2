package me.choicore.samples.operation.domain

import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class MeteringStrategyRegistry(
    private val dayOfWeekStrategies: List<DayOfWeekMeteringStrategy>,
    private val specificDateStrategies: List<SpecifiedDateMeteringStrategy>,
) : Meter {
    private val daysOfWeek =
        dayOfWeekStrategies
            .groupBy { it.dayOfWeek }
            .mapValues { (_, strategies: List<DayOfWeekMeteringStrategy>) ->
                strategies.sortedByDescending { it.effectiveDate }
            }

    private val specifies: Map<LocalDate, SpecifiedDateMeteringStrategy> =
        specificDateStrategies.associateBy { it.specifiedDate }

    private val cache = ConcurrentHashMap<LocalDate, MeteringStrategy?>()

    override fun measure(measurand: Measurand): List<Measurement> = getMeteringStrategy(measurand.measureOn).measure(measurand)

    fun getMeteringStrategy(measuredOn: LocalDate): MeteringStrategy {
        return cache.computeIfAbsent(measuredOn) { date ->
            specifies[date]?.let<SpecifiedDateMeteringStrategy, Nothing> {
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
