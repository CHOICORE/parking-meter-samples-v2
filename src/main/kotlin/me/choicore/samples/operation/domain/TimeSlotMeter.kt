package me.choicore.samples.operation.domain

class TimeSlotMeter : Meter {
    val measurers: List<TimeSlotMeasurer>

    constructor(measurers: List<TimeSlotMeasurer> = emptyList()) {
        this.measurers = TimeSlotMeasurerFactory.fullest(measurers)
    }

    constructor(vararg measurer: TimeSlotMeasurer) : this(measurers = measurer.toList())

    override fun measure(measurand: Measurand): List<Measurement> = this.measurers.map { it.measure(measurand) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeSlotMeter) return false

        if (measurers != other.measurers) return false

        return true
    }

    override fun hashCode(): Int = measurers.hashCode()

    override fun toString(): String = "TimeSlotMeter(measurers=$measurers)"
}
