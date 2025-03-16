package me.choicore.samples.operation

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalTime

data class MeteringPeriod(
    val range: TimeSlot,
    val rate: Double,
) : Measurer {
    override fun measure(measurand: Measurand): Measurement {
        val (_, from: LocalTime, to: LocalTime) = measurand
        val intersect: TimeSlot? = range.intersect(startTimeInclusive = from, endTimeExclusive = to)

        if (intersect == null) {
            log.debug("No intersection between metering period and measurand")
            return Measurement(
                measurand = measurand,
                measurer = this,
                measurement = 0,
                factor = this.rate,
            )
        }

        return Measurement(
            measurand = measurand,
            measurer = this,
            measurement = intersect.duration.toMinutes(),
            factor = this.rate,
        )
    }

    companion object {
        val log: Logger = getLogger(MeteringPeriod::class.java)
        val STANDARD: MeteringPeriod = MeteringPeriod(TimeSlot.ALL_DAY, 1.0)

        fun standard(range: TimeSlot): MeteringPeriod = MeteringPeriod(range, 1.0)
    }
}
