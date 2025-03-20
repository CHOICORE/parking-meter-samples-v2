package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.Auditable
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import java.time.LocalDate
import java.time.LocalDateTime

data class DayOfWeekMeteringStrategyEntity(
    val id: PrimaryKey,
    val lotId: ForeignKey,
    override val timeSlotMeter: TimeSlotMeter,
    override val effectiveDate: LocalDate,
    override val registeredAt: LocalDateTime = LocalDateTime.now(),
    override val registeredBy: String,
    override var modifiedAt: LocalDateTime? = null,
    override var modifiedBy: String? = null,
    override var deletedAt: LocalDateTime? = null,
    override var deletedBy: String? = null,
) : DayOfWeekMeteringStrategy(timeSlotMeter = timeSlotMeter, effectiveDate = effectiveDate),
    Auditable
