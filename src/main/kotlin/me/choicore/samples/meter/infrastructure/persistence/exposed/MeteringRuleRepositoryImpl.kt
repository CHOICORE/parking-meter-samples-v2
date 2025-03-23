package me.choicore.samples.meter.infrastructure.persistence.exposed

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringRuleRepository
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy.SpecifiedDateBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategyRegistry
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleEntity
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleTable
import org.jetbrains.exposed.sql.Exists
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.notExists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.unionAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class MeteringRuleRepositoryImpl :
    MeteringRuleRepository,
    TimeBasedMeteringStrategyRegistry {
    @Transactional
    override fun save(meteringRule: MeteringRule): MeteringRule =
        when (val strategy: TimeBasedMeteringStrategy = meteringRule.timeBasedMeteringStrategy) {
            is DayOfWeekBasedMeteringStrategy -> {
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

            is SpecifiedDateBasedMeteringStrategy -> {
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

    @Transactional(readOnly = true)
    override fun existsBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
    ): Boolean {
        val exists: Exists =
            exists(
                MeteringRuleTable
                    .select(intLiteral(1))
                    .where {
                        (MeteringRuleTable.lotId eq 1) and
                            (MeteringRuleTable.effectiveDate eq LocalDate.now()) and
                            (MeteringRuleTable.meteringMode inList MeteringMode.entries) and
                            (MeteringRuleTable.deletedAt.isNull())
                    },
            )
        val resultRow: ResultRow = Table.Dual.select(exists).first()
        return resultRow[exists]
    }

    @Transactional
    override fun deleteById(id: PrimaryKey) {
        MeteringRuleEntity.findByIdAndUpdate(id.value) {
            it.deletedAt = LocalDateTime.now()
            it.deletedBy = AuditorContext.identifier
        }
    }

    @Transactional(readOnly = true)
    override fun getAvailableTimeBasedMeteringStrategy(
        lotId: ForeignKey,
        measureOn: LocalDate,
    ): TimeBasedMeteringStrategy? {
        val once =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId.value) and
                        (MeteringRuleTable.effectiveDate eq measureOn) and
                        (MeteringRuleTable.meteringMode eq ONCE) and
                        (MeteringRuleTable.deletedAt.isNull())
                }

        val repeat =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId.value) and
                        (MeteringRuleTable.effectiveDate lessEq measureOn) and
                        (MeteringRuleTable.meteringMode eq REPEAT) and
                        notExists(
                            MeteringRuleTable
                                .select(intLiteral(1))
                                .where {
                                    (MeteringRuleTable.lotId eq lotId.value) and
                                        (MeteringRuleTable.effectiveDate eq measureOn) and
                                        (MeteringRuleTable.meteringMode eq ONCE) and
                                        (MeteringRuleTable.deletedAt.isNull())
                                },
                        )
                }.orderBy(MeteringRuleTable.effectiveDate, DESC)
                .limit(1)

        return once.unionAll(repeat).singleOrNull()?.convert()
    }

    @Transactional(readOnly = true)
    override fun findBy(
        lotId: ForeignKey,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
    ): List<MeteringRule> =
        when (meteringMode) {
            ONCE ->
                MeteringRuleEntity
                    .find {
                        (MeteringRuleTable.lotId eq lotId.value) and
                            (MeteringRuleTable.effectiveDate lessEq effectiveDate) and
                            (MeteringRuleTable.meteringMode eq meteringMode) and
                            MeteringRuleTable.deletedAt.isNull()
                    }.map(MeteringRuleEntity::convert)

            REPEAT ->
                MeteringRuleEntity
                    .find {
                        (MeteringRuleTable.lotId eq lotId.value) and
                            (MeteringRuleTable.effectiveDate eq effectiveDate) and
                            (MeteringRuleTable.meteringMode eq meteringMode) and
                            MeteringRuleTable.deletedAt.isNull()
                    }.map(MeteringRuleEntity::convert)
        }

    private fun ResultRow.convert(): MeteringRule {
        val timelineMeteringStrategy =
            when (this[MeteringRuleTable.meteringMode]) {
                REPEAT -> {
                    DayOfWeekBasedMeteringStrategy(
                        effectiveDate = this[MeteringRuleTable.effectiveDate],
                        timelineMeter = this[MeteringRuleTable.timelineMeter],
                    )
                }

                ONCE ->
                    SpecifiedDateBasedMeteringStrategy(
                        effectiveDate = this[MeteringRuleTable.effectiveDate],
                        timelineMeter = this[MeteringRuleTable.timelineMeter],
                    )
            }

        val meteringRule =
            MeteringRule(
                id = PrimaryKey(this[MeteringRuleTable.id].value),
                lotId = ForeignKey(this[MeteringRuleTable.lotId]),
                timeBasedMeteringStrategy = timelineMeteringStrategy,
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
