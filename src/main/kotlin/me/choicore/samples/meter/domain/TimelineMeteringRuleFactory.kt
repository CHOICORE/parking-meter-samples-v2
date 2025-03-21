package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import java.time.LocalDate

object TimelineMeteringRuleFactory {
    fun create(
        lotId: Long,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
        timeSlotMeasurers: List<TimeSlotMeasurer>,
    ): TimelineMeteringRule {
        val timelineMeter = TimelineMeter(timeSlotMeasurers)
        val timelineMeteringStrategy =
            when (meteringMode) {
                REPEAT -> {
                    DayOfWeekMeteringStrategy(
                        effectiveDate = effectiveDate,
                        timelineMeter = timelineMeter,
                    )
                }

                ONCE -> {
                    SpecifiedDateMeteringStrategy(
                        effectiveDate = effectiveDate,
                        timelineMeter = timelineMeter,
                    )
                }
            }

        return TimelineMeteringRule(
            lotId = ForeignKey(lotId),
            timelineMeteringStrategy = timelineMeteringStrategy,
        )
    }
}
