package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import java.time.DayOfWeek
import java.time.LocalDate

sealed interface TimelineMeteringStrategy : Meter {
    val timelineMeter: TimelineMeter
    val effectiveDate: LocalDate
    val meteringMode: MeteringMode

    fun applies(measuredOn: LocalDate): Boolean

    abstract class AbstractTimelineMeteringStrategy(
        override val timelineMeter: TimelineMeter = TimelineMeter.STANDARD,
    ) : TimelineMeteringStrategy {
        override fun measure(measurand: Measurand): List<Metric> = this.timelineMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        override val effectiveDate: LocalDate,
    ) : AbstractTimelineMeteringStrategy(timelineMeter = timelineMeter) {
        override val meteringMode: MeteringMode = ONCE

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.effectiveDate
    }

    data class DayOfWeekMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        override val effectiveDate: LocalDate,
    ) : AbstractTimelineMeteringStrategy(timelineMeter = timelineMeter) {
        override val meteringMode: MeteringMode = REPEAT
        val dayOfWeek: DayOfWeek = effectiveDate.dayOfWeek

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
