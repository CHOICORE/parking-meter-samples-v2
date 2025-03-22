package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.PrimaryKey
import org.springframework.stereotype.Service

@Service
class MeteringRuleRegistrar(
    private val meteringRuleRepository: MeteringRuleRepository,
) {
    fun register(meteringRule: MeteringRule): MeteringRule = meteringRuleRepository.save(meteringRule)

    fun unregister(id: PrimaryKey) {
        meteringRuleRepository.deleteById(id)
    }
}
