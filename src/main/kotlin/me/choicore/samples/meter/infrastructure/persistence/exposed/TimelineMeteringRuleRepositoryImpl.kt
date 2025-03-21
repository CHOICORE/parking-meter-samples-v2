package me.choicore.samples.meter.infrastructure.persistence.exposed

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.TimelineMeteringRule
import me.choicore.samples.meter.domain.TimelineMeteringRuleRepository
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.TimelineMeteringRuleTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class TimelineMeteringRuleRepositoryImpl : TimelineMeteringRuleRepository {
    @Transactional
    override fun save(timelineMeteringRule: TimelineMeteringRule): TimelineMeteringRule {
        when (timelineMeteringRule.timelineMeteringStrategy) {
            is DayOfWeekMeteringStrategy -> TODO()
            is SpecifiedDateMeteringStrategy -> TODO()
            else -> throw UnsupportedOperationException("Unsupported timeline metering strategy")
        }
    }

    override fun findBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
        vararg meteringMode: MeteringMode,
    ): List<TimelineMeteringRule> =
        TimelineMeteringRuleTable
            .selectAll()
            .where {
                (TimelineMeteringRuleTable.lotId eq lotId.value) and
                    (TimelineMeteringRuleTable.effectiveDate eq effectiveDate) and
                    (TimelineMeteringRuleTable.meteringMode inList meteringMode.toSet()) and
                    (TimelineMeteringRuleTable.deletedAt.isNull())
            }.map { it.toTimelineMeteringRule() }

    private fun ResultRow.toTimelineMeteringRule(): TimelineMeteringRule {
        val timelineMeteringStrategy =
            when (this[TimelineMeteringRuleTable.meteringMode]) {
                REPEAT -> {
                    DayOfWeekMeteringStrategy(
                        effectiveDate = this[TimelineMeteringRuleTable.effectiveDate],
                        timelineMeter = this[TimelineMeteringRuleTable.timelineMeter],
                    )
                }

                ONCE ->
                    SpecifiedDateMeteringStrategy(
                        effectiveDate = this[TimelineMeteringRuleTable.effectiveDate],
                        timelineMeter = this[TimelineMeteringRuleTable.timelineMeter],
                    )
            }

        val timelineMeteringRule =
            TimelineMeteringRule(
                id = PrimaryKey(this[TimelineMeteringRuleTable.id].value),
                lotId = ForeignKey(this[TimelineMeteringRuleTable.lotId]),
                timelineMeteringStrategy = timelineMeteringStrategy,
                registeredAt = this[TimelineMeteringRuleTable.registeredAt],
                registeredBy = this[TimelineMeteringRuleTable.registeredBy],
            )
        timelineMeteringRule.modifiedAt = this[TimelineMeteringRuleTable.lastModifiedAt]
        timelineMeteringRule.modifiedBy = this[TimelineMeteringRuleTable.lastModifiedBy]
        timelineMeteringRule.deletedAt = this[TimelineMeteringRuleTable.deletedAt]
        timelineMeteringRule.deletedBy = this[TimelineMeteringRuleTable.deletedBy]
        return timelineMeteringRule
    }
}
