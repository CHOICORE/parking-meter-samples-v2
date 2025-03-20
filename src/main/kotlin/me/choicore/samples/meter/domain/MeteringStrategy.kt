package me.choicore.samples.meter.domain

import java.time.DayOfWeek
import java.time.LocalDate

sealed interface MeteringStrategy : Meter {
    fun applies(measuredOn: LocalDate): Boolean

    sealed interface TimeSlotMeteringStrategy : MeteringStrategy {
        val timeSlotMeter: TimeSlotMeter
    }

    abstract class AllDayMeteringStrategy(
        override val timeSlotMeter: TimeSlotMeter,
    ) : TimeSlotMeteringStrategy {
        override fun measure(measurand: Measurand): List<Metric> = this.timeSlotMeter.measure(measurand = measurand)
    }

    open class SpecifiedDateMeteringStrategy(
        override val timeSlotMeter: TimeSlotMeter,
        open val specifiedDate: LocalDate,
    ) : AllDayMeteringStrategy(timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate
    }

    open class DayOfWeekMeteringStrategy(
        override val timeSlotMeter: TimeSlotMeter,
        open val effectiveDate: LocalDate,
    ) : AllDayMeteringStrategy(timeSlotMeter = timeSlotMeter) {
        val dayOfWeek: DayOfWeek get() = effectiveDate.dayOfWeek

        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
