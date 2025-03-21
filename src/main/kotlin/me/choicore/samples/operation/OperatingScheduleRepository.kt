package me.choicore.samples.operation

import me.choicore.samples.operation.OperatingSchedule.RepeatMode
import java.time.LocalDate

interface OperatingScheduleRepository {
    fun save(operatingSchedule: OperatingSchedule): OperatingSchedule

    fun findBy(
        lotId: Long,
        effectiveDate: LocalDate,
        vararg mode: RepeatMode,
    ): List<OperatingSchedule>
}
