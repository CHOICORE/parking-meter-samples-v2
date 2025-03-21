package me.choicore.samples.meter

import java.time.DayOfWeek
import java.time.LocalDate

sealed interface TimelineMeteringStrategy : Meter {
    val timelineMeter: TimelineMeter

    fun applies(measuredOn: LocalDate): Boolean

    abstract class AbstractTimelineMeteringStrategy(
        override val timelineMeter: TimelineMeter = TimelineMeter.STANDARD,
    ) : TimelineMeteringStrategy {
        override fun measure(measurand: Measurand): List<Metric> = this.timelineMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        val specifiedDate: LocalDate,
    ) : AbstractTimelineMeteringStrategy(timelineMeter = timelineMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate
    }

    data class DayOfWeekMeteringStrategy(
        override val timelineMeter: TimelineMeter,
        val effectiveDate: LocalDate,
    ) : AbstractTimelineMeteringStrategy(timelineMeter = timelineMeter) {
        val dayOfWeek: DayOfWeek get() = effectiveDate.dayOfWeek

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
