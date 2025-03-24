package me.choicore.samples.meter.infrastructure.persistence.exposed

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringRuleRepository
import me.choicore.samples.meter.domain.MeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.SpecifiedDateBasedMeteringStrategy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleTable
import me.choicore.samples.support.exposed.SELECT_ONE
import me.choicore.samples.support.exposed.exists
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.notExists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.unionAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class MeteringRuleRepositoryImpl : MeteringRuleRepository {
    @Transactional
    override fun save(meteringRule: MeteringRule): MeteringRule =
        when (val strategy: MeteringStrategy = meteringRule.meteringStrategy) {
            is DayOfWeekBasedMeteringStrategy ->
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
                    MeteringRuleTable.Entity.findByIdAndUpdate(meteringRule.id.value) {
                        it.effectiveDate = strategy.effectiveDate
                        it.dayOfWeek = strategy.dayOfWeek
                        it.timelineMeter = strategy.timelineMeter
                        it.lastModifiedAt = meteringRule.modifiedAt
                        it.lastModifiedBy = meteringRule.modifiedBy
                    }
                    meteringRule
                }

            is SpecifiedDateBasedMeteringStrategy ->
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
                    MeteringRuleTable.Entity.findByIdAndUpdate(meteringRule.id.value) {
                        it.timelineMeter = strategy.timelineMeter
                        it.effectiveDate = strategy.effectiveDate
                        it.lastModifiedAt = meteringRule.modifiedAt
                        it.lastModifiedBy = meteringRule.modifiedBy
                    }
                    meteringRule
                }

            else -> throw UnsupportedOperationException("Unsupported timeline metering strategy")
        }

    @Transactional(readOnly = true)
    override fun existsBy(
        lotId: SecondaryKey,
        effectiveDate: LocalDate,
    ): Boolean =
        MeteringRuleTable.exists {
            (MeteringRuleTable.lotId eq lotId.value) and
                (MeteringRuleTable.effectiveDate eq effectiveDate) and
                (MeteringRuleTable.meteringMode inList MeteringMode.entries) and
                (MeteringRuleTable.deletedAt.isNull())
        }

    @Transactional
    override fun deleteById(id: PrimaryKey) {
        MeteringRuleTable.Entity.findByIdAndUpdate(id.value) {
            it.deletedAt = LocalDateTime.now()
            it.deletedBy = AuditorContext.identifier
        }
    }

    @Transactional(readOnly = true)
    override fun getAvailableTimeBasedMeteringStrategy(
        lotId: SecondaryKey,
        measureOn: LocalDate,
    ): MeteringStrategy? {
        val once: Query =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId.value) and
                        (MeteringRuleTable.effectiveDate eq measureOn) and
                        (MeteringRuleTable.meteringMode eq ONCE) and
                        (MeteringRuleTable.deletedAt.isNull())
                }

        val repeat: Query =
            MeteringRuleTable
                .selectAll()
                .where {
                    (MeteringRuleTable.lotId eq lotId.value) and
                        (MeteringRuleTable.effectiveDate lessEq measureOn) and
                        (MeteringRuleTable.meteringMode eq REPEAT) and
                        notExists(
                            MeteringRuleTable
                                .select(SELECT_ONE)
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
        lotId: SecondaryKey,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
    ): List<MeteringRule> =
        when (meteringMode) {
            ONCE ->
                MeteringRuleTable.Entity
                    .find {
                        (MeteringRuleTable.lotId eq lotId.value) and
                            (MeteringRuleTable.effectiveDate lessEq effectiveDate) and
                            (MeteringRuleTable.meteringMode eq meteringMode) and
                            MeteringRuleTable.deletedAt.isNull()
                    }.map(MeteringRuleTable.Entity::convert)

            REPEAT ->
                MeteringRuleTable.Entity
                    .find {
                        (MeteringRuleTable.lotId eq lotId.value) and
                            (MeteringRuleTable.effectiveDate eq effectiveDate) and
                            (MeteringRuleTable.meteringMode eq meteringMode) and
                            MeteringRuleTable.deletedAt.isNull()
                    }.map(MeteringRuleTable.Entity::convert)
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
                lotId = SecondaryKey(this[MeteringRuleTable.lotId]),
                meteringStrategy = timelineMeteringStrategy,
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
