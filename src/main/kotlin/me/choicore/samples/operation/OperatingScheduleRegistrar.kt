package me.choicore.samples.operation

import org.springframework.stereotype.Service

@Service
class OperatingScheduleRegistrar(
    private val operatingScheduleRepository: OperatingScheduleRepository,
) {
    fun register(operatingSchedule: OperatingSchedule): OperatingSchedule {
        val registered: OperatingSchedule = operatingScheduleRepository.save(operatingSchedule = operatingSchedule)
        return registered
    }
}
