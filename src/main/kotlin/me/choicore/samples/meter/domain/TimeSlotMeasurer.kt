package me.choicore.samples.meter.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalTime

data class TimeSlotMeasurer(
    val timeSlot: TimeSlot,
    val weight: Double,
) : Measurer {
    override fun measure(measurand: Measurand): Metric? {
        val (_, from: LocalTime, to: LocalTime) = measurand
        val intersect: TimeSlot? = this.timeSlot.intersect(startTimeInclusive = from, endTimeExclusive = to)

        if (intersect == null) {
            log.debug("No intersection between metering period and measurand")
            return null
        }

        return Metric(
            measurand = measurand,
            measurer = this,
            usage = intersect.duration,
        )
    }

    companion object {
        private const val FIXED_WEIGHT = 1.0
        val log: Logger = LoggerFactory.getLogger(TimeSlotMeasurer::class.java)
        val STANDARD: TimeSlotMeasurer = TimeSlotMeasurer(timeSlot = TimeSlot.ALL_DAY, weight = FIXED_WEIGHT)

        fun standard(timeSlot: TimeSlot): TimeSlotMeasurer =
            TimeSlotMeasurer(
                timeSlot = timeSlot,
                weight = FIXED_WEIGHT,
            )
    }
}
