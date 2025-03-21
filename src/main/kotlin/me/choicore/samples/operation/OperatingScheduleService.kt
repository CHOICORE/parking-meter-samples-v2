package me.choicore.samples.operation

import me.choicore.samples.meter.domain.TimeSlotMeasurer
import me.choicore.samples.operation.OperatingSchedule.RepeatMode
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class OperatingScheduleService(
    private val operatingScheduleRegistrar: OperatingScheduleRegistrar,
    private val operatingScheduleValidator: OperatingScheduleValidator,
) {
    fun register(
        lotId: Long,
        mode: RepeatMode,
        effectiveDate: LocalDate,
        measurers: List<TimeSlotMeasurer>,
    ): Long {
        val operatingSchedule: OperatingSchedule =
            OperatingScheduleFactory.create(
                lotId = lotId,
                mode = mode,
                effectiveDate = effectiveDate,
                timeSlotMeasurers = measurers,
            )
        operatingScheduleValidator.validate(operatingSchedule)
        val registered: OperatingSchedule = operatingScheduleRegistrar.register(operatingSchedule = operatingSchedule)
        return registered.id
    }
}
