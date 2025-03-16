package me.choicore.samples.operation

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalTime

data class MeteringPeriod(
    val range: TimeSlot,
    val rate: BigDecimal,
) : Measurer {
    override fun measure(measurand: Measurand): Measurement {
        val (_, from: LocalTime, to: LocalTime) = measurand
        val intersect: TimeSlot? = range.intersect(startTimeInclusive = from, endTimeExclusive = to)

        if (intersect == null) {
            log.debug("No intersection between metering period and measurand")
            return Measurement(measurand = measurand, measurer = this, measurement = Duration.ZERO)
        }

        return Measurement(measurand = measurand, measurer = this, measurement = intersect.duration)
    }

    companion object {
        val log: Logger = getLogger(MeteringPeriod::class.java)
        val STANDARD: MeteringPeriod = MeteringPeriod(TimeSlot.ALL_DAY, BigDecimal.valueOf(100))
    }
}
