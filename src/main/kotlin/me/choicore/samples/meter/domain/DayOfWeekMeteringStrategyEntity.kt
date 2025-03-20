package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.Auditable
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.TimeSlotMeteringStrategy
import java.time.LocalDateTime

data class DayOfWeekMeteringStrategyEntity(
    val id: PrimaryKey,
    val lotId: ForeignKey,
    val strategy: DayOfWeekMeteringStrategy,
    override val registeredAt: LocalDateTime = LocalDateTime.now(),
    override val registeredBy: String,
    override var modifiedAt: LocalDateTime? = null,
    override var modifiedBy: String? = null,
    override var deletedAt: LocalDateTime? = null,
    override var deletedBy: String? = null,
) : TimeSlotMeteringStrategy by strategy,
    Auditable
