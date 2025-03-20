package me.choicore.samples.operation.presentation

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.operation.application.OperatingScheduleManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/lots")
class OperatingScheduleEndpoints(
    private val operatingScheduleManager: OperatingScheduleManager,
) {
    @PostMapping("/{lotId}/schedules")
    fun register(
        @PathVariable lotId: Long,
        @RequestBody request: OperatingScheduleRequest.Registration,
    ): ResponseEntity<*> {
        AuditorContext(request.registrant) {
            operatingScheduleManager.register(
                lotId = lotId,
                repeatMode = request.mode,
                effectiveDate = request.effectiveDate,
                timeline = request.toTimeline(),
            )
        }

        TODO()
    }

    @PatchMapping("/{lotId}/schedules/{scheduleId}")
    fun modify(
        @PathVariable lotId: Long,
        @PathVariable scheduleId: Long,
        @RequestBody request: OperatingScheduleRequest.Modification,
    ): ResponseEntity<*> {
        AuditorContext(request.modifier) {
            operatingScheduleManager.modify(
                scheduleId = scheduleId,
                lotId = lotId,
                repeatMode = request.mode,
                effectiveDate = request.effectiveDate,
                timeline = request.toTimeline(),
            )
        }

        TODO()
    }

    @GetMapping("/{lotId}/schedules")
    fun getSchedules(
        @PathVariable lotId: Long,
    ): ResponseEntity<*> {
        TODO()
    }
}
