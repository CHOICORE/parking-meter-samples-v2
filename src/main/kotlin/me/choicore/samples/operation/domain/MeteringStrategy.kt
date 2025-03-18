package me.choicore.samples.operation.domain

import java.time.DayOfWeek
import java.time.LocalDate

sealed interface MeteringStrategy : Meter {
    val id: Long
    val lotId: Long

    fun applies(measuredOn: LocalDate): Boolean

    sealed interface TimeSlotMeteringStrategy : MeteringStrategy {
        val timeSlotMeter: TimeSlotMeter
    }

    sealed class AllDayMeteringStrategy(
        override val id: Long,
        override val lotId: Long,
        override val timeSlotMeter: TimeSlotMeter,
    ) : TimeSlotMeteringStrategy {
        override fun measure(measurand: Measurand): List<Measurement> = this.timeSlotMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateMeteringStrategy(
        override val id: Long = 0,
        override val lotId: Long,
        override val timeSlotMeter: TimeSlotMeter,
        val specifiedDate: LocalDate,
    ) : AllDayMeteringStrategy(id = id, lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate
    }

    data class DayOfWeekMeteringStrategy(
        override val id: Long = 0,
        override val lotId: Long,
        override val timeSlotMeter: TimeSlotMeter,
        val dayOfWeek: DayOfWeek,
        val effectiveDate: LocalDate,
    ) : AllDayMeteringStrategy(id = id, lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
