package me.choicore.samples.operation

import java.time.LocalDate

data class MeteringSpecificDateStrategy(
    val specifiedDate: LocalDate,
    val meteringHours: List<MeteringPeriod>,
) : TimeBasedMeteringStrategy {
    override fun applies(measuredOn: LocalDate): Boolean = measuredOn == specifiedDate
}
