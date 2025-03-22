package me.choicore.samples.meter.infrastructure.persistence.exposed

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringRuleRepository
import me.choicore.samples.meter.domain.TimelineMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleEntity
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class MeteringRuleRepositoryImpl : MeteringRuleRepository {
    @Transactional
    override fun save(meteringRule: MeteringRule): MeteringRule =
        when (val strategy: TimelineMeteringStrategy = meteringRule.timelineMeteringStrategy) {
            is DayOfWeekMeteringStrategy -> {
                if (meteringRule.id == PrimaryKey.UNINITIALIZED) {
                    val registered: Long =
                        MeteringRuleTable
                            .insertAndGetId {
                                it[lotId] = meteringRule.lotId.value
                                it[meteringMode] = REPEAT
                                it[effectiveDate] = strategy.effectiveDate
                                it[dayOfWeek] = strategy.dayOfWeek
                                it[timelineMeter] = strategy.timelineMeter
                                it[registeredAt] = meteringRule.registeredAt
                                it[registeredBy] = meteringRule.registeredBy
                            }.value

                    meteringRule.copy(id = PrimaryKey(value = registered))
                } else {
                    MeteringRuleEntity.findByIdAndUpdate(meteringRule.id.value) {
                        it.effectiveDate = strategy.effectiveDate
                        it.dayOfWeek = strategy.dayOfWeek
                        it.timelineMeter = strategy.timelineMeter
                        it.lastModifiedAt = meteringRule.modifiedAt
                        it.lastModifiedBy = meteringRule.modifiedBy
                    }
                    meteringRule
                }
            }

            is SpecifiedDateMeteringStrategy -> {
                if (meteringRule.id == PrimaryKey.UNINITIALIZED) {
                    val registered: Long =
                        MeteringRuleTable
                            .insertAndGetId {
                                it[lotId] = meteringRule.lotId.value
                                it[meteringMode] = ONCE
                                it[effectiveDate] = strategy.effectiveDate
                                it[timelineMeter] = strategy.timelineMeter
                                it[registeredAt] = meteringRule.registeredAt
                                it[registeredBy] = meteringRule.registeredBy
                            }.value

                    meteringRule.copy(id = PrimaryKey(value = registered))
                } else {
                    MeteringRuleEntity.findByIdAndUpdate(meteringRule.id.value) {
                        it.timelineMeter = strategy.timelineMeter
                        it.effectiveDate = strategy.effectiveDate
                        it.lastModifiedAt = meteringRule.modifiedAt
                        it.lastModifiedBy = meteringRule.modifiedBy
                    }
                    meteringRule
                }
            }

            else -> {
                throw UnsupportedOperationException("Unsupported timeline metering strategy")
            }
        }

    @Transactional
    override fun deleteById(id: PrimaryKey) {
        MeteringRuleEntity.findByIdAndUpdate(id.value) {
            it.deletedAt = LocalDateTime.now()
            it.deletedBy = AuditorContext.identifier
        }
    }

    @Transactional(readOnly = true)
    override fun findBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
        vararg meteringMode: MeteringMode,
    ): List<MeteringRule> =
        MeteringRuleEntity
            .find {
                (MeteringRuleTable.lotId eq lotId.value) and
                    (MeteringRuleTable.effectiveDate eq effectiveDate) and
                    (MeteringRuleTable.meteringMode inList meteringMode.toSet()) and
                    (MeteringRuleTable.deletedAt.isNull())
            }.map(MeteringRuleEntity::convert)

    private fun ResultRow.convert(): MeteringRule {
        val timelineMeteringStrategy =
            when (this[MeteringRuleTable.meteringMode]) {
                REPEAT -> {
                    DayOfWeekMeteringStrategy(
                        effectiveDate = this[MeteringRuleTable.effectiveDate],
                        timelineMeter = this[MeteringRuleTable.timelineMeter],
                    )
                }

                ONCE ->
                    SpecifiedDateMeteringStrategy(
                        effectiveDate = this[MeteringRuleTable.effectiveDate],
                        timelineMeter = this[MeteringRuleTable.timelineMeter],
                    )
            }

        val meteringRule =
            MeteringRule(
                id = PrimaryKey(this[MeteringRuleTable.id].value),
                lotId = ForeignKey(this[MeteringRuleTable.lotId]),
                timelineMeteringStrategy = timelineMeteringStrategy,
                registeredAt = this[MeteringRuleTable.registeredAt],
                registeredBy = this[MeteringRuleTable.registeredBy],
            )
        meteringRule.modifiedAt = this[MeteringRuleTable.lastModifiedAt]
        meteringRule.modifiedBy = this[MeteringRuleTable.lastModifiedBy]
        meteringRule.deletedAt = this[MeteringRuleTable.deletedAt]
        meteringRule.deletedBy = this[MeteringRuleTable.deletedBy]
        return meteringRule
    }
}
