package me.choicore.samples.meter.domain

import org.springframework.stereotype.Component

@Component
class MeteringRuleValidator(
    private val meteringRuleRepository: MeteringRuleRepository,
) {
    fun validate(meteringRule: MeteringRule) {
        val found: List<MeteringRule> =
            meteringRuleRepository.findBy(
                lotId = meteringRule.lotId,
                effectiveDate = meteringRule.effectiveDate,
                meteringMode = arrayOf(meteringRule.meteringMode),
            )

        require(found.isEmpty()) { "이미 등록된 운영일정이 있습니다." }
    }
}
