package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.Auditable
import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import java.time.LocalDateTime

data class MeteringRule(
    val id: PrimaryKey = PrimaryKey.UNINITIALIZED,
    val lotId: SecondaryKey,
    val meteringStrategy: MeteringStrategy,
    override val registeredAt: LocalDateTime = LocalDateTime.now(),
    override val registeredBy: String = AuditorContext.identifier,
) : MeteringStrategy by meteringStrategy,
    Auditable {
    override var modifiedAt: LocalDateTime? = null
    override var modifiedBy: String? = null
    override var deletedAt: LocalDateTime? = null
    override var deletedBy: String? = null

    override fun toString(): String =
        "MeteringRule(id=$id, lotId=$lotId, meteringStrategy=$meteringStrategy, registeredAt=$registeredAt, registeredBy='$registeredBy', modifiedAt=$modifiedAt, modifiedBy=$modifiedBy, deletedAt=$deletedAt, deletedBy=$deletedBy)"
}
