package me.choicore.samples.meter.domain.deprecated

import me.choicore.samples.meter.domain.Measurand
import me.choicore.samples.meter.domain.Meter
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.AbstractMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.SpecifiedDateBasedMeteringStrategy
import me.choicore.samples.meter.domain.Metric
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Deprecated("unused")
data class TimeBasedMeteringStrategyRegistry(
    val dayOfWeekStrategies: List<DayOfWeekBasedMeteringStrategy>,
    val specificDateStrategies: List<SpecifiedDateBasedMeteringStrategy>,
) : Meter {
    constructor(strategies: List<MeteringStrategy>) : this(
        dayOfWeekStrategies = strategies.filterIsInstance<DayOfWeekBasedMeteringStrategy>(),
        specificDateStrategies = strategies.filterIsInstance<SpecifiedDateBasedMeteringStrategy>(),
    )

    constructor(vararg strategy: MeteringStrategy) : this(strategy.toList())

    private val daysOfWeek: Map<DayOfWeek, List<DayOfWeekBasedMeteringStrategy>> =
        this.dayOfWeekStrategies
            .groupBy(DayOfWeekBasedMeteringStrategy::dayOfWeek)
            .mapValues { (_, strategies: List<DayOfWeekBasedMeteringStrategy>) ->
                strategies.sortedByDescending(DayOfWeekBasedMeteringStrategy::effectiveDate)
            }

    private val specifies: Map<LocalDate, SpecifiedDateBasedMeteringStrategy> =
        this.specificDateStrategies.associateBy(SpecifiedDateBasedMeteringStrategy::effectiveDate)

    private val cache = ConcurrentHashMap<LocalDate, MeteringStrategy?>()

    override fun measure(measurand: Measurand): List<Metric> =
        getTimelineMeteringStrategy(measuredOn = measurand.measureOn)?.measure(measurand = measurand)
            ?: DEFAULT.measure(measurand = measurand)

    private fun getTimelineMeteringStrategy(measuredOn: LocalDate): MeteringStrategy? {
        return this.cache.computeIfAbsent(measuredOn) { date ->
            this.specifies[date]?.let { return@computeIfAbsent it }
            val strategies: List<DayOfWeekBasedMeteringStrategy>? = this.daysOfWeek[date.dayOfWeek]
            if (strategies != null) {
                for (strategy: DayOfWeekBasedMeteringStrategy in strategies) {
                    if (strategy.applies(date)) {
                        return@computeIfAbsent strategy
                    }
                }
            }
            null
        }
    }

    companion object {
        val DEFAULT: MeteringStrategy =
            object : AbstractMeteringStrategy() {
                override val effectiveDate: LocalDate
                    get() = LocalDate.now()
                override val meteringMode: MeteringMode
                    get() = ONCE

                override fun applies(measuredOn: LocalDate): Boolean = true
            }
    }
}
