package me.choicore.samples.meter.domain

import org.springframework.stereotype.Service

@Service
class TimelineMeteringRuleRegistrar(
    private val timelineMeteringRuleRepository: TimelineMeteringRuleRepository,
) {
    fun register(timelineMeteringRule: TimelineMeteringRule): TimelineMeteringRule =
        timelineMeteringRuleRepository.save(timelineMeteringRule)
}
