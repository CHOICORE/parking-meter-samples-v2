package me.choicore.samples.meter.presentation.request

import me.choicore.samples.meter.application.RepeatMode
import me.choicore.samples.meter.domain.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

sealed interface OperatingScheduleRequest {
    val mode: RepeatMode
    val effectiveDate: LocalDate
    val timeline: List<TimeSlotRequest>

    data class Registration(
        override val mode: RepeatMode,
        override val effectiveDate: LocalDate,
        override val timeline: List<TimeSlotRequest>,
        val registrant: String,
    ) : OperatingScheduleRequest

    data class Modification(
        val id: Long,
        override val mode: RepeatMode,
        override val effectiveDate: LocalDate,
        override val timeline: List<TimeSlotRequest>,
        val modifier: String,
    ) : OperatingScheduleRequest

    data class TimeSlotRequest(
        val startTime: LocalTime,
        val endTime: LocalTime,
        val factor: Int,
    )

    // FIXME: 중복 검증 및 반환 타입, 로직 위치 변경 예정
    fun toTimeline(): List<Pair<TimeSlot, Int>> {
        val sorted: List<TimeSlotRequest> = timeline.sortedBy { it.startTime }
        for (i in 0 until sorted.size - 1) {
            val current = sorted[i]
            val next = sorted[i + 1]
            if (current.endTime > next.startTime) {
                throw IllegalArgumentException("Overlapping time slots are not allowed: ${current.endTime} > ${next.startTime}")
            }
        }

        return sorted.map { slot ->
            TimeSlot(slot.startTime, slot.endTime) to slot.factor
        }
    }
}
