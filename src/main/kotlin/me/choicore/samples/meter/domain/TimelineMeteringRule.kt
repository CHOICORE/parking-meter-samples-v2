package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.Auditable
import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import java.time.LocalDateTime

class TimelineMeteringRule(
    val id: PrimaryKey = PrimaryKey.UNINITIALIZED,
    val lotId: ForeignKey,
    val timelineMeteringStrategy: TimelineMeteringStrategy,
    override val registeredAt: LocalDateTime = LocalDateTime.now(),
    override val registeredBy: String = AuditorContext.identifier,
) : TimelineMeteringStrategy by timelineMeteringStrategy,
    Auditable {
    override var modifiedAt: LocalDateTime? = null
    override var modifiedBy: String? = null
    override var deletedAt: LocalDateTime? = null
    override var deletedBy: String? = null
}
