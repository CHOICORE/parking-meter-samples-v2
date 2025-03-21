package me.choicore.samples.meter.domain

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TimelineMeteringRuleService(
    private val timelineMeteringRuleValidator: TimelineMeteringRuleValidator,
    private val timelineMeteringRuleRegistrar: TimelineMeteringRuleRegistrar,
) {
    fun register(
        lotId: Long,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
        measurers: List<TimeSlotMeasurer>,
    ): Long {
        val timelineMeteringRule: TimelineMeteringRule =
            TimelineMeteringRuleFactory.create(
                lotId = lotId,
                meteringMode = meteringMode,
                effectiveDate = effectiveDate,
                timeSlotMeasurers = measurers,
            )

        timelineMeteringRuleValidator.validate(timelineMeteringRule)

        return timelineMeteringRuleRegistrar.register(timelineMeteringRule).id.value
    }
}
