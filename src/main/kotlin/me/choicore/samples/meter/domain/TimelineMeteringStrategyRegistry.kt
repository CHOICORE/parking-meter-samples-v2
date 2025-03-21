package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.AbstractTimelineMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class TimelineMeteringStrategyRegistry(
    val dayOfWeekStrategies: List<DayOfWeekMeteringStrategy>,
    val specificDateStrategies: List<SpecifiedDateMeteringStrategy>,
) : Meter {
    constructor(strategies: List<TimelineMeteringStrategy>) : this(
        dayOfWeekStrategies = strategies.filterIsInstance<DayOfWeekMeteringStrategy>(),
        specificDateStrategies = strategies.filterIsInstance<SpecifiedDateMeteringStrategy>(),
    )

    constructor(vararg strategy: TimelineMeteringStrategy) : this(strategy.toList())

    private val daysOfWeek: Map<DayOfWeek, List<DayOfWeekMeteringStrategy>> =
        this.dayOfWeekStrategies
            .groupBy(DayOfWeekMeteringStrategy::dayOfWeek)
            .mapValues { (_, strategies: List<DayOfWeekMeteringStrategy>) ->
                strategies.sortedByDescending(DayOfWeekMeteringStrategy::effectiveDate)
            }

    private val specifies: Map<LocalDate, SpecifiedDateMeteringStrategy> =
        this.specificDateStrategies.associateBy(SpecifiedDateMeteringStrategy::effectiveDate)

    private val cache = ConcurrentHashMap<LocalDate, TimelineMeteringStrategy?>()

    override fun measure(measurand: Measurand): List<Metric> =
        getTimelineMeteringStrategy(measuredOn = measurand.measureOn)?.measure(measurand = measurand)
            ?: DEFAULT.measure(measurand = measurand)

    private fun getTimelineMeteringStrategy(measuredOn: LocalDate): TimelineMeteringStrategy? {
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
        }
    }

    companion object {
        val DEFAULT: TimelineMeteringStrategy =
            object : AbstractTimelineMeteringStrategy() {
                override val effectiveDate: LocalDate
                    get() = LocalDate.now()
                override val meteringMode: MeteringMode
                    get() = ONCE

                override fun applies(measuredOn: LocalDate): Boolean = true
            }
    }
}
