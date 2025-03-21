package me.choicore.samples.meter.domain

import org.springframework.stereotype.Component

@Component
class TimelineMeteringRuleValidator(
    private val timelineMeteringRuleRepository: TimelineMeteringRuleRepository,
) {
    fun validate(timelineMeteringRule: TimelineMeteringRule) {
        val found: List<TimelineMeteringRule> =
            timelineMeteringRuleRepository.findBy(
                lotId = timelineMeteringRule.lotId,
                effectiveDate = timelineMeteringRule.effectiveDate,
                meteringMode = arrayOf(timelineMeteringRule.meteringMode),
            )

        require(found.isEmpty()) { "이미 등록된 운영 일정이 있습니다." }
    }
}
