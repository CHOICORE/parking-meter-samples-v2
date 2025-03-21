package me.choicore.samples.operation

import org.springframework.stereotype.Component

@Component
class OperatingScheduleValidator(
    private val operatingScheduleRepository: OperatingScheduleRepository,
) {
    fun validate(operatingSchedule: OperatingSchedule) {
        val found: List<OperatingSchedule> =
            operatingScheduleRepository.findBy(
                lotId = operatingSchedule.lotId,
                effectiveDate = operatingSchedule.effectiveDate,
                mode = arrayOf(operatingSchedule.mode),
            )

        require(found.isEmpty()) { "이미 등록된 운영 일정이 있습니다." }
    }
}
