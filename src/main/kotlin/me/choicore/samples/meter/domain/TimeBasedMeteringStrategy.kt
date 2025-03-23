package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import java.time.DayOfWeek
import java.time.LocalDate

sealed interface TimeBasedMeteringStrategy : Meter {
    val timelineMeter: TimelineMeter
    val effectiveDate: LocalDate
    val meteringMode: MeteringMode

    fun applies(measuredOn: LocalDate): Boolean

    abstract class AbstractTimeBasedMeteringStrategy(
        override val timelineMeter: TimelineMeter = TimelineMeter.STANDARD,
    ) : TimeBasedMeteringStrategy {
        override fun measure(measurand: Measurand): List<Metric> = this.timelineMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateBasedMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        override val effectiveDate: LocalDate,
    ) : AbstractTimeBasedMeteringStrategy(timelineMeter = timelineMeter) {
        override val meteringMode: MeteringMode = ONCE

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.effectiveDate
    }

    data class DayOfWeekBasedMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        override val effectiveDate: LocalDate,
    ) : AbstractTimeBasedMeteringStrategy(timelineMeter = timelineMeter) {
        override val meteringMode: MeteringMode = REPEAT
        val dayOfWeek: DayOfWeek = effectiveDate.dayOfWeek

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
