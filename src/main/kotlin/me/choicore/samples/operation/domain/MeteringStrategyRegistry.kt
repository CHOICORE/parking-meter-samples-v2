package me.choicore.samples.operation.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class MeteringStrategyRegistry(
    private val dayOfWeekStrategies: List<MeteringStrategy.DayOfWeekMeteringStrategy>,
    private val specificDateStrategies: List<MeteringStrategy.SpecifiedDateMeteringStrategy>,
) : Meter {
    private val daysOfWeek: Map<DayOfWeek, List<MeteringStrategy.DayOfWeekMeteringStrategy>> =
        dayOfWeekStrategies
            .groupBy { it.dayOfWeek }
            .mapValues { (_, strategies: List<MeteringStrategy.DayOfWeekMeteringStrategy>) ->
                strategies.sortedByDescending { it.effectiveDate }
            }

    private val specifies: Map<LocalDate, MeteringStrategy.SpecifiedDateMeteringStrategy> =
        specificDateStrategies.associateBy { it.specifiedDate }

    private val cache = ConcurrentHashMap<LocalDate, MeteringStrategy?>()

    override fun measure(measurand: Measurand): List<Measurement> = getMeteringStrategy(measurand.measureOn).measure(measurand)

    fun getMeteringStrategy(measuredOn: LocalDate): MeteringStrategy {
        return cache.computeIfAbsent(measuredOn) { date ->
            specifies[date]?.let {
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
