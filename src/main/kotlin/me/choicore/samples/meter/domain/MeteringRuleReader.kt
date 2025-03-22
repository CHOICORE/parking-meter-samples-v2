package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import org.springframework.stereotype.Service

@Service
class MeteringRuleReader(
    private val meteringRuleRepository: MeteringRuleRepository,
) {
    fun getDaysOfWeekByLotId(lotId: ForeignKey): List<MeteringRule> {
        TODO()
    }

    fun getSpecifiedDaysByLotId(lotId: ForeignKey): List<MeteringRule> {
        TODO()
    }
}
