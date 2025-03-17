package me.choicore.samples.operation.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class DayOfWeekMeteringStrategy(
    override val lotId: Long,
    val dayOfWeek: DayOfWeek,
    val timeSlotMeter: TimeSlotMeter,
    val effectiveDate: LocalDate,
) : MeteringStrategy {
    override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate

    override fun measure(measurand: Measurand): List<Measurement> = timeSlotMeter.measure(measurand)
}
