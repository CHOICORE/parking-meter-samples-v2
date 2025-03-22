package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.PrimaryKey
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MeteringRuleService(
    private val meteringRuleValidator: MeteringRuleValidator,
    private val meteringRuleRegistrar: MeteringRuleRegistrar,
) {
    fun register(
        lotId: Long,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
        measurers: List<TimeSlotMeasurer>,
    ): Long {
        val meteringRule: MeteringRule =
            MeteringRuleFactory.create(
                lotId = lotId,
                meteringMode = meteringMode,
                effectiveDate = effectiveDate,
                timeSlotMeasurers = measurers,
            )

        meteringRuleValidator.validate(meteringRule)

        return meteringRuleRegistrar.register(meteringRule).id.value
    }

    fun unregister(id: Long) {
        meteringRuleRegistrar.unregister(PrimaryKey(id))
    }
}
