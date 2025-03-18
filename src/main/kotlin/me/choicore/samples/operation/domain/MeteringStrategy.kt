package me.choicore.samples.operation.domain

import java.time.DayOfWeek
import java.time.LocalDate

sealed interface MeteringStrategy : Meter {
    val lotId: Long

    fun applies(measuredOn: LocalDate): Boolean

    sealed class AllDayMeteringStrategy(
        override val lotId: Long,
        open val timeSlotMeter: TimeSlotMeter,
    ) : MeteringStrategy {
        override fun measure(measurand: Measurand): List<Measurement> = this.timeSlotMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateMeteringStrategy(
        override val lotId: Long,
        override val timeSlotMeter: TimeSlotMeter,
        val specifiedDate: LocalDate,
    ) : AllDayMeteringStrategy(lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate
    }

    data class DayOfWeekMeteringStrategy(
        override val lotId: Long,
        override val timeSlotMeter: TimeSlotMeter,
        val dayOfWeek: DayOfWeek,
        val effectiveDate: LocalDate,
    ) : AllDayMeteringStrategy(lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
