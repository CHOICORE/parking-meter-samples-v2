package me.choicore.samples.operation.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalTime

data class TimeSlotMeasurer(
    val timeSlot: TimeSlot,
    val weight: Double,
) : Measurer {
    override fun measure(measurand: Measurand): Measurement {
        val (_, from: LocalTime, to: LocalTime) = measurand
        val intersect: TimeSlot? = timeSlot.intersect(startTimeInclusive = from, endTimeExclusive = to)

        if (intersect == null) {
            log.debug("No intersection between metering period and measurand")
            return Measurement(
                measurand = measurand,
                measurer = this,
                measurement = 0,
                factor = this.weight,
            )
        }

        return Measurement(
            measurand = measurand,
            measurer = this,
            measurement = intersect.duration.toMinutes(),
            factor = this.weight,
        )
    }

    companion object {
        val log: Logger = getLogger(TimeSlotMeasurer::class.java)
        val STANDARD: TimeSlotMeasurer = TimeSlotMeasurer(TimeSlot.ALL_DAY, 1.0)

        fun standard(timeSlot: TimeSlot): TimeSlotMeasurer = TimeSlotMeasurer(timeSlot, 1.0)
    }
}
