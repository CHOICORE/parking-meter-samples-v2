package me.choicore.samples.operation.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.time.Duration
import java.time.LocalTime

private const val FIXED_WEIGHT = 1.0

data class TimeSlotMeasurer(
    val timeSlot: TimeSlot,
    val weight: Double,
) : Measurer {
    override fun measure(measurand: Measurand): Measurement {
        val (_, from: LocalTime, to: LocalTime) = measurand
        val intersect: TimeSlot? = this.timeSlot.intersect(startTimeInclusive = from, endTimeExclusive = to)

        if (intersect == null) {
            log.debug("No intersection between metering period and measurand")
            return Measurement(
                measurand = measurand,
                measurer = this,
                measurement = Duration.ZERO,
                factor = this.weight,
            )
        }

        return Measurement(
            measurand = measurand,
            measurer = this,
            measurement = intersect.duration,
            factor = this.weight,
        )
    }

    companion object {
        val log: Logger = getLogger(TimeSlotMeasurer::class.java)
        val STANDARD: TimeSlotMeasurer = TimeSlotMeasurer(timeSlot = TimeSlot.ALL_DAY, weight = FIXED_WEIGHT)

        fun standard(timeSlot: TimeSlot): TimeSlotMeasurer =
            TimeSlotMeasurer(
                timeSlot = timeSlot,
                weight = FIXED_WEIGHT,
            )
    }
}
