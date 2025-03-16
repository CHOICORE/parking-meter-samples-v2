package me.choicore.samples.operation

import me.choicore.samples.operation.dsl.Timeline
import java.math.BigDecimal

object MeteringPeriodFactory {
    fun fullest(source: List<MeteringPeriod>): List<MeteringPeriod> {
        if (source.isEmpty()) {
            return listOf(MeteringPeriod.STANDARD)
        }

        val additional =
            Timeline {
                source.forEach { slot(it.range) }
            }.run {
                this.unset.map {
                    MeteringPeriod(it, BigDecimal.valueOf(100))
                }
            }

        return (source + additional)
            .sortedBy { it.range.startTimeInclusive }
    }
}
