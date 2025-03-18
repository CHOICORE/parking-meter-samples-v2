package me.choicore.samples.operation.domain

import me.choicore.samples.operation.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy.SpecifiedDateMeteringStrategy
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class MeteringStrategyRegistry(
    private val dayOfWeekStrategies: List<DayOfWeekMeteringStrategy>,
    private val specificDateStrategies: List<SpecifiedDateMeteringStrategy>,
) : Meter {
    private val daysOfWeek: Map<DayOfWeek, List<DayOfWeekMeteringStrategy>> =
        this.dayOfWeekStrategies
            .groupBy(DayOfWeekMeteringStrategy::dayOfWeek)
            .mapValues { (_, strategies: List<DayOfWeekMeteringStrategy>) ->
                strategies.sortedByDescending(DayOfWeekMeteringStrategy::effectiveDate)
            }

    private val specifies: Map<LocalDate, SpecifiedDateMeteringStrategy> =
        this.specificDateStrategies.associateBy(SpecifiedDateMeteringStrategy::specifiedDate)

    private val cache = ConcurrentHashMap<LocalDate, MeteringStrategy?>()

    override fun measure(measurand: Measurand): List<Measurement> =
        getMeteringStrategy(measuredOn = measurand.measureOn).measure(
            measurand = measurand,
        )

    fun getMeteringStrategy(measuredOn: LocalDate): MeteringStrategy {
        return this.cache.computeIfAbsent(measuredOn) { date ->
            this.specifies[date]?.let { return@computeIfAbsent it }
            val strategies: List<DayOfWeekMeteringStrategy>? = this.daysOfWeek[date.dayOfWeek]
            if (strategies != null) {
                for (strategy: DayOfWeekMeteringStrategy in strategies) {
                    if (strategy.applies(date)) {
                        return@computeIfAbsent strategy
                    }
                }
            }
            null
        } ?: throw NoSuchElementException("No metering strategy found for $measuredOn")
    }
}
