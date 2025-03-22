package me.choicore.samples.meter.infrastructure.persistence.exposed.table

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeteringRuleEntity(
    id: EntityID<Long>,
) : LongEntity(id = id) {
    companion object : LongEntityClass<MeteringRuleEntity>(MeteringRuleTable)

    val lotId by MeteringRuleTable.lotId
    val meteringMode by MeteringRuleTable.meteringMode
    var effectiveDate by MeteringRuleTable.effectiveDate
    var dayOfWeek by MeteringRuleTable.dayOfWeek
    var timelineMeter by MeteringRuleTable.timelineMeter
    val registeredAt by MeteringRuleTable.registeredAt
    val registeredBy by MeteringRuleTable.registeredBy
    var lastModifiedAt by MeteringRuleTable.lastModifiedAt
    var lastModifiedBy by MeteringRuleTable.lastModifiedBy
    var deletedAt by MeteringRuleTable.deletedAt
    var deletedBy by MeteringRuleTable.deletedBy

    fun convert(): MeteringRule =
        MeteringRule(
            id = PrimaryKey(this.id.value),
            lotId = ForeignKey(this.lotId),
            timelineMeteringStrategy =
                when (this.meteringMode) {
                    REPEAT -> {
                        DayOfWeekMeteringStrategy(
                            effectiveDate = this.effectiveDate,
                            timelineMeter = this.timelineMeter,
                        )
                    }

                    ONCE ->
                        SpecifiedDateMeteringStrategy(
                            effectiveDate = this.effectiveDate,
                            timelineMeter = this.timelineMeter,
                        )
                },
            registeredAt = this.registeredAt,
            registeredBy = this.registeredBy,
        ).also {
            it.modifiedAt = this.lastModifiedAt
            it.modifiedBy = this.lastModifiedBy
            it.deletedAt = this.deletedAt
            it.deletedBy = this.deletedBy
        }
}
