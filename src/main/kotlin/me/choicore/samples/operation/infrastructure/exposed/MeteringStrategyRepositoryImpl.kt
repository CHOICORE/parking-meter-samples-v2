package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.MeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy.SpecifiedDateMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategyType.DAY_OF_WEEK
import me.choicore.samples.operation.domain.MeteringStrategyType.SPECIFIED_DATE
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MeteringStrategyRepositoryImpl {
    fun save(meteringStrategy: MeteringStrategy): Long {
        when (meteringStrategy) {
            is DayOfWeekMeteringStrategy -> {
                return transaction {
                    MeteringStrategyTable
                        .insertAndGetId {
                            it[lotId] = meteringStrategy.lotId
                            it[dayOfWeek] = meteringStrategy.dayOfWeek
                            it[strategyType] = DAY_OF_WEEK
                            it[effectiveDate] = meteringStrategy.effectiveDate
                            it[data] = meteringStrategy.timeSlotMeter
                            it[registeredAt] = LocalDateTime.now()
                            it[registeredBy] = "system"
                        }.value
                }
            }

            is SpecifiedDateMeteringStrategy -> {
                return transaction {
                    MeteringStrategyTable
                        .insertAndGetId {
                            it[lotId] = meteringStrategy.lotId
                            it[dayOfWeek] = null
                            it[strategyType] = SPECIFIED_DATE
                            it[effectiveDate] = null
                            it[specificDate] = meteringStrategy.specifiedDate
                            it[data] = meteringStrategy.timeSlotMeter
                            it[registeredAt] = LocalDateTime.now()
                            it[registeredBy] = "system"
                        }.value
                }
            }

            else -> {
                throw RuntimeException("Unsupported metering strategy: $meteringStrategy")
            }
        }
    }

    fun findByLotId(lotId: Long): List<MeteringStrategy> =
        transaction {
            MeteringStrategyTable
                .selectAll()
                .where {
                    MeteringStrategyTable.lotId eq lotId
                }.map { it.toMeteringStrategy() }
        }

    fun getDayOfWeekMeteringStrategies(): List<DayOfWeekMeteringStrategy> {
        TODO()
    }

    fun getSpecifiedDayMeteringStrategies(): List<SpecifiedDateMeteringStrategy> {
        TODO()
    }

    private fun ResultRow.toMeteringStrategy(): MeteringStrategy {
        val lotId = this[MeteringStrategyTable.lotId]
        val dayOfWeek = this[MeteringStrategyTable.dayOfWeek]
        val strategyType = this[MeteringStrategyTable.strategyType]
        val effectiveDate = this[MeteringStrategyTable.effectiveDate]
        val timeSlotMeter = this[MeteringStrategyTable.data]
        return when (strategyType) {
            SPECIFIED_DATE ->
                SpecifiedDateMeteringStrategy(
                    lotId = lotId,
                    specifiedDate = this[MeteringStrategyTable.specificDate]!!,
                    timeSlotMeter = timeSlotMeter,
                )

            DAY_OF_WEEK ->
                DayOfWeekMeteringStrategy(
                    lotId = lotId,
                    dayOfWeek = dayOfWeek!!,
                    timeSlotMeter = timeSlotMeter,
                    effectiveDate = effectiveDate!!,
                )
        }
    }
}
