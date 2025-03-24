package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.SpecifiedDateBasedMeteringStrategy
import java.time.LocalDate

object MeteringRuleFactory {
    fun create(
        lotId: Long,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
        timeSlotMeasurers: List<TimeSlotMeasurer>,
    ): MeteringRule {
        val timelineMeter = TimelineMeter(timeSlotMeasurers)
        val timelineMeteringStrategy =
            when (meteringMode) {
                REPEAT -> {
                    DayOfWeekBasedMeteringStrategy(
                        effectiveDate = effectiveDate,
                        timelineMeter = timelineMeter,
                    )
                }

                ONCE -> {
                    SpecifiedDateBasedMeteringStrategy(
                        effectiveDate = effectiveDate,
                        timelineMeter = timelineMeter,
                    )
                }
            }

        return MeteringRule(
            lotId = SecondaryKey(lotId),
            meteringStrategy = timelineMeteringStrategy,
        )
    }
}
