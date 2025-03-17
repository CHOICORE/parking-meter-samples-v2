package me.choicore.samples.operation.domain

data class TimeSlotMeter(
    private val measurers: List<TimeSlotMeasurer> = TimeSlotMeterFactory.fullest(emptyList()),
) : Meter {
    override fun measure(measurand: Measurand): List<Measurement> = this.measurers.map { it.measure(measurand) }
}
