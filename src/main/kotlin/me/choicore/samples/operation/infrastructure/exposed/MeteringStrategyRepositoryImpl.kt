package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.context.entity.AuditorContext
import me.choicore.samples.operation.context.entity.ForeignKey
import me.choicore.samples.operation.context.entity.PrimaryKey
import me.choicore.samples.operation.domain.MeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy.SpecifiedDateMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategyRepository
import me.choicore.samples.operation.domain.MeteringStrategyType.DAY_OF_WEEK
import me.choicore.samples.operation.domain.MeteringStrategyType.SPECIFIED_DATE
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MeteringStrategyRepositoryImpl : MeteringStrategyRepository {
    override fun save(meteringStrategy: MeteringStrategy): PrimaryKey {
        when (meteringStrategy) {
            is DayOfWeekMeteringStrategy -> {
                if (meteringStrategy.id == PrimaryKey.UNASSIGNED) {
                    return transaction {
                        MeteringStrategyTable
                            .insertAndGetId {
                                it[lotId] = meteringStrategy.lotId.value
                                it[dayOfWeek] = meteringStrategy.dayOfWeek
                                it[strategyType] = DAY_OF_WEEK
                                it[effectiveDate] = meteringStrategy.effectiveDate
                                it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                                it[registeredAt] = LocalDateTime.now()
                                it[registeredBy] = AuditorContext.identifier
                            }.toPrimaryKey()
                    }
                } else {
                    return transaction {
                        MeteringStrategyTable
                            .update({ MeteringStrategyTable.id eq meteringStrategy.id.value }) {
                                it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                                it[lastModifiedAt] = LocalDateTime.now()
                                it[lastModifiedBy] = AuditorContext.identifier
                            }
                        meteringStrategy.id
                    }
                }
            }

            is SpecifiedDateMeteringStrategy -> {
                if (meteringStrategy.id == PrimaryKey.UNASSIGNED) {
                    return transaction {
                        MeteringStrategyTable
                            .insertAndGetId {
                                it[lotId] = meteringStrategy.lotId.value
                                it[strategyType] = SPECIFIED_DATE
                                it[specificDate] = meteringStrategy.specifiedDate
                                it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                                it[registeredAt] = LocalDateTime.now()
                                it[registeredBy] = AuditorContext.identifier
                            }.toPrimaryKey()
                    }
                } else {
                    return transaction {
                        MeteringStrategyTable
                            .update({ MeteringStrategyTable.id eq meteringStrategy.id.value }) {
                                it[lotId] = meteringStrategy.lotId.value
                                it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                                it[lastModifiedAt] = LocalDateTime.now()
                                it[lastModifiedBy] = AuditorContext.identifier
                            }
                        meteringStrategy.id
                    }
                }
            }
        }
    }

    override fun findByLotId(lotId: ForeignKey): List<MeteringStrategy> =
        transaction {
            MeteringStrategyTable
                .selectAll()
                .where {
                    MeteringStrategyTable.lotId eq lotId.value
                }.map { it.toMeteringStrategy() }
        }

    private fun EntityID<Long>.toPrimaryKey(): PrimaryKey = PrimaryKey(this.value)

    private fun EntityID<Long>.toForeignKey(): ForeignKey = ForeignKey(this.value)

    private fun ResultRow.toMeteringStrategy(): MeteringStrategy {
        val id = this[MeteringStrategyTable.id]
        val lotId = this[MeteringStrategyTable.lotId]
        val dayOfWeek = this[MeteringStrategyTable.dayOfWeek]
        val strategyType = this[MeteringStrategyTable.strategyType]
        val effectiveDate = this[MeteringStrategyTable.effectiveDate]
        val timeSlotMeter = this[MeteringStrategyTable.timeSlotMeter]
        val specifiedDate = this[MeteringStrategyTable.specificDate]
        return when (strategyType) {
            SPECIFIED_DATE -> {
                SpecifiedDateMeteringStrategy(
                    id = id.toPrimaryKey(),
                    lotId = ForeignKey(lotId),
                    specifiedDate = specifiedDate!!,
                    timeSlotMeter = timeSlotMeter,
                )
            }

            DAY_OF_WEEK ->
                DayOfWeekMeteringStrategy(
                    id = id.toPrimaryKey(),
                    lotId = ForeignKey(lotId),
                    dayOfWeek = dayOfWeek!!,
                    timeSlotMeter = timeSlotMeter,
                    effectiveDate = effectiveDate!!,
                )
        }
    }
}
