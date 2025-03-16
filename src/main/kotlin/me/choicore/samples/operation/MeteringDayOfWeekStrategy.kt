package me.choicore.samples.operation

import java.time.DayOfWeek
import java.time.LocalDate

data class MeteringDayOfWeekStrategy(
    val dayOfWeek: DayOfWeek,
    val meteringHours: MeteringHours,
    val effectiveDate: LocalDate,
) : TimeBasedMeteringStrategy {
    override fun applies(measuredOn: LocalDate): Boolean = measuredOn.dayOfWeek == this.dayOfWeek && measuredOn >= this.effectiveDate
}
