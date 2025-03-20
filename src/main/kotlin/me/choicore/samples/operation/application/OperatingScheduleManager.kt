package me.choicore.samples.operation.application

import me.choicore.samples.meter.domain.TimeSlot
import me.choicore.samples.operation.domain.RepeatMode
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class OperatingScheduleManager {
    fun register(
        lotId: Long,
        repeatMode: RepeatMode,
        effectiveDate: LocalDate,
        timeline: List<Pair<TimeSlot, Int>>,
    ) {
        TODO()
    }

    fun modify(
        scheduleId: Long,
        lotId: Long,
        repeatMode: RepeatMode,
        effectiveDate: LocalDate,
        timeline: List<Pair<TimeSlot, Int>>,
    ) {
        TODO()
    }
}
