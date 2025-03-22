package me.choicore.samples.meter.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalTime

data class TimeSlotMeasurer(
    val timeSlot: TimeSlot,
    val calibration: Calibration,
) : Measurer {
    constructor(timeSlot: TimeSlot) : this(timeSlot, Calibration.IDENTITY)

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
        val log: Logger = LoggerFactory.getLogger(TimeSlotMeasurer::class.java)
        val STANDARD: TimeSlotMeasurer =
            TimeSlotMeasurer(timeSlot = TimeSlot.ALL_DAY, calibration = Calibration.IDENTITY)

        fun standard(timeSlot: TimeSlot): TimeSlotMeasurer =
            TimeSlotMeasurer(
                timeSlot = timeSlot,
                calibration = Calibration.IDENTITY,
            )
    }
}
