package me.choicore.samples.meter.domain

import org.springframework.stereotype.Component

@Component
class MeteringRuleValidator(
    private val meteringRuleRepository: MeteringRuleRepository,
) {
    fun validate(meteringRule: MeteringRule) {
        meteringRuleRepository.existsBy(
            lotId = meteringRule.lotId,
            effectiveDate = meteringRule.effectiveDate,
        )
    }
}
