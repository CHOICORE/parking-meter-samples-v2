package me.choicore.samples.operation.domain

import java.time.LocalDate

data class SpecifiedDateMeteringStrategy(
    override val lotId: Long,
    val specifiedDate: LocalDate,
    val timeSlotMeter: TimeSlotMeter,
) : MeteringStrategy {
    override fun applies(measuredOn: LocalDate): Boolean = measuredOn == this.specifiedDate

    override fun measure(measurand: Measurand): List<Measurement> = this.timeSlotMeter.measure(measurand)
}
