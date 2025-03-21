package me.choicore.samples.operation

import me.choicore.samples.meter.TimeSlotMeasurer
import me.choicore.samples.meter.TimelineMeter
import me.choicore.samples.operation.OperatingSchedule.DayOfWeekOperatingSchedule
import me.choicore.samples.operation.OperatingSchedule.RepeatMode
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.ONCE
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.REPEAT
import me.choicore.samples.operation.OperatingSchedule.SpecifiedDateOperatingSchedule
import java.time.LocalDate

object OperatingScheduleFactory {
    fun create(
        lotId: Long,
        mode: RepeatMode,
        effectiveDate: LocalDate,
        timeSlotMeasurers: List<TimeSlotMeasurer>,
    ): OperatingSchedule {
        val timelineMeter = TimelineMeter(timeSlotMeasurers)
        return when (mode) {
            REPEAT -> {
                DayOfWeekOperatingSchedule(
                    lotId = lotId,
                    effectiveDate = effectiveDate,
                    timelineMeter = timelineMeter,
                )
            }

            ONCE ->
                SpecifiedDateOperatingSchedule(
                    lotId = lotId,
                    effectiveDate = effectiveDate,
                    timelineMeter = timelineMeter,
                )
        }
    }
}
