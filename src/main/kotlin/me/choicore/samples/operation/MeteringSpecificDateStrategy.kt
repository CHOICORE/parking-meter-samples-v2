package me.choicore.samples.operation

import java.time.LocalDate

data class MeteringSpecificDateStrategy(
    val specifiedDate: LocalDate,
    val meteringHours: MeteringHours,
) : TimeBasedMeteringStrategy {
    override fun applies(measuredOn: LocalDate): Boolean = measuredOn == specifiedDate

    override fun measure(measurand: Measurand): List<Measurement> = meteringHours.periods.map { period -> period.measure(measurand) }
}
