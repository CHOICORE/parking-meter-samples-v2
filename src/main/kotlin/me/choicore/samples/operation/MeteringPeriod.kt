package me.choicore.samples.operation

import java.math.BigDecimal

data class MeteringPeriod(
    val range: TimeSlot,
    val rate: BigDecimal,
) : Measurer {
    companion object {
        val STANDARD = MeteringPeriod(TimeSlot.ALL_DAY, BigDecimal.valueOf(100))
    }

    override fun measure(measurand: Measurand): Measurement {
        //    val intersect = range.intersect(startTimeInclusive, endTimeExclusive)
        TODO()
    }
}
