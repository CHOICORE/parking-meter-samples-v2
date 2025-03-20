package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.dsl.Timeline

class TimeSlotMeter : Meter {
    val measurers: List<TimeSlotMeasurer>

    constructor(measurers: List<TimeSlotMeasurer> = emptyList()) {
        this.measurers = TimeSlotMeterResolver.resolve(measurers)
    }

    constructor(vararg measurer: TimeSlotMeasurer) : this(measurers = measurer.toList())

    override fun measure(measurand: Measurand): List<Metric> = this.measurers.mapNotNull { it.measure(measurand) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeSlotMeter) return false

        if (this.measurers != other.measurers) return false

        return true
    }

    override fun hashCode(): Int = this.measurers.hashCode()

    override fun toString(): String = "TimeSlotMeter(measurers=$measurers)"

    private object TimeSlotMeterResolver {
        fun resolve(source: List<TimeSlotMeasurer>): List<TimeSlotMeasurer> {
            if (source.isEmpty()) {
                return listOf(TimeSlotMeasurer.STANDARD)
            }
            val additional: List<TimeSlotMeasurer> =
                Timeline { source.forEach { slot(it.timeSlot) } }
                    .run { this.unset.map { TimeSlotMeasurer.standard(it) } }

            return (source + additional)
                .sortedBy { it.timeSlot.startTimeInclusive }
        }
    }
}
