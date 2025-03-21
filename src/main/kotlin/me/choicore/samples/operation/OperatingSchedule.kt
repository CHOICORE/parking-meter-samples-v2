package me.choicore.samples.operation

import me.choicore.samples.context.entity.Auditable
import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.meter.domain.TimelineMeter
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.ONCE
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.REPEAT
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface OperatingSchedule {
    val id: Long
    val lotId: Long
    val mode: RepeatMode
    val timelineMeter: TimelineMeter
    val effectiveDate: LocalDate

    data class DayOfWeekOperatingSchedule(
        override val id: Long = 0,
        override val lotId: Long,
        override val effectiveDate: LocalDate, // validFrom
        override val timelineMeter: TimelineMeter,
        override val registeredAt: LocalDateTime = LocalDateTime.now(),
        override val registeredBy: String = AuditorContext.identifier,
    ) : OperatingSchedule,
        Auditable {
        override val mode: RepeatMode = REPEAT
        val dayOfWeek: DayOfWeek = effectiveDate.dayOfWeek
        override var modifiedAt: LocalDateTime? = null
        override var modifiedBy: String? = null
        override var deletedAt: LocalDateTime? = null
        override var deletedBy: String? = null
    }

    data class SpecifiedDateOperatingSchedule(
        override val id: Long = 0,
        override val lotId: Long,
        override val effectiveDate: LocalDate,
        override val timelineMeter: TimelineMeter,
        override val registeredAt: LocalDateTime = LocalDateTime.now(),
        override val registeredBy: String = AuditorContext.identifier,
    ) : OperatingSchedule,
        Auditable {
        override val mode: RepeatMode = ONCE
        override var modifiedAt: LocalDateTime? = null
        override var modifiedBy: String? = null
        override var deletedAt: LocalDateTime? = null
        override var deletedBy: String? = null
    }

    enum class RepeatMode {
        REPEAT,
        ONCE,
    }
}
