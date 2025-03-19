package me.choicore.samples.operation.domain

import me.choicore.samples.operation.context.entity.ForeignKey
import me.choicore.samples.operation.context.entity.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate

sealed interface MeteringStrategy : Meter {
    fun applies(measuredOn: LocalDate): Boolean

    sealed interface TimeSlotMeteringStrategy : MeteringStrategy {
        val timeSlotMeter: TimeSlotMeter
    }

    sealed class AllDayMeteringStrategy(
        open val id: PrimaryKey,
        open val lotId: ForeignKey,
        override val timeSlotMeter: TimeSlotMeter,
    ) : TimeSlotMeteringStrategy {
        override fun measure(measurand: Measurand): List<Measurement> = this.timeSlotMeter.measure(measurand = measurand)
    }

    data class SpecifiedDateMeteringStrategy(
        override val id: PrimaryKey = PrimaryKey.UNASSIGNED,
        override val lotId: ForeignKey,
        override val timeSlotMeter: TimeSlotMeter,
        val specifiedDate: LocalDate,
    ) : AllDayMeteringStrategy(id = id, lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate
    }

    data class DayOfWeekMeteringStrategy(
        override val id: PrimaryKey = PrimaryKey.UNASSIGNED,
        override val lotId: ForeignKey,
        override val timeSlotMeter: TimeSlotMeter,
        val dayOfWeek: DayOfWeek,
        val effectiveDate: LocalDate,
    ) : AllDayMeteringStrategy(id = id, lotId = lotId, timeSlotMeter = timeSlotMeter) {
        override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
    }
}
