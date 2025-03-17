package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategyType.DAY_OF_WEEK
import me.choicore.samples.operation.domain.MeteringStrategyType.SPECIFIED_DATE
import me.choicore.samples.operation.domain.SpecifiedDateMeteringStrategy
import me.choicore.samples.operation.domain.TimeSlotMeter
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.data
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.dayOfWeek
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.effectiveDate
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.registeredAt
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.registeredBy
import me.choicore.samples.operation.infrastructure.exposed.MeteringStrategyTable.strategyType
import org.jetbrains.exposed.sql.insertAndGetId
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
                            it[data] = meteringStrategy.timeSlotMeter.measurers
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
                            it[data] = meteringStrategy.timeSlotMeter.measurers
                            it[registeredAt] = LocalDateTime.now()
                            it[registeredBy] = "system"
                        }.value
                }
            }

            else -> throw RuntimeException("Unsupported metering strategy: $meteringStrategy")
        }
    }

    fun findByLotId(lotId: Long): List<MeteringStrategy> =
        transaction {
            MeteringStrategyTable
                .select(MeteringStrategyTable.columns)
                .where {
                    MeteringStrategyTable.lotId eq lotId
                }.map {
                    it[MeteringStrategyTable.lotId]
                    it[dayOfWeek]
                    it[strategyType]
                    it[effectiveDate]
                    it[data]
                    it[registeredAt]
                    it[registeredBy]
                    when (it[strategyType]) {
                        SPECIFIED_DATE ->
                            SpecifiedDateMeteringStrategy(
                                lotId = it[MeteringStrategyTable.lotId],
                                specifiedDate = it[MeteringStrategyTable.specificDate]!!,
                                timeSlotMeter = TimeSlotMeter(it[data]),
                            )

                        DAY_OF_WEEK ->
                            DayOfWeekMeteringStrategy(
                                lotId = it[MeteringStrategyTable.lotId],
                                dayOfWeek = it[dayOfWeek]!!,
                                timeSlotMeter = TimeSlotMeter(it[data]),
                                effectiveDate = it[effectiveDate]!!,
                            )
                    }
                }
        }

    fun getDayOfWeekMeteringStrategies(): List<DayOfWeekMeteringStrategy> {
        TODO()
    }

    fun getSpecifiedDayMeteringStrategies(): List<SpecifiedDateMeteringStrategy> {
        TODO()
    }
}
