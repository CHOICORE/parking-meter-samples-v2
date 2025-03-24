package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.dsl.Timeline

class TimelineMeter(
    measurers: List<TimeSlotMeasurer> = emptyList(),
) : Meter {
    val measurers: List<TimeSlotMeasurer>

    constructor(vararg measurer: TimeSlotMeasurer) : this(measurers = measurer.toList())

    init {
        this.measurers = TimelineMeterResolver.resolve(measurers)
    }

    override fun measure(measurand: Measurand): List<Metric> = this.measurers.mapNotNull { it.measure(measurand) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimelineMeter) return false

        if (this.measurers != other.measurers) return false

        return true
    }

    override fun hashCode(): Int = this.measurers.hashCode()

    override fun toString(): String = "TimelineMeter(measurers=$measurers)"

    object TimelineMeterResolver {
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

    companion object {
        val STANDARD: TimelineMeter = TimelineMeter()
    }
}
