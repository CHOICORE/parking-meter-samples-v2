package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategy
import me.choicore.samples.operation.domain.MeteringStrategyType.DAY_OF_WEEK
import me.choicore.samples.operation.domain.MeteringStrategyType.SPECIFIED_DATE
import me.choicore.samples.operation.domain.SpecifiedDateMeteringStrategy
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class MeteringStrategyRepositoryImpl {
    fun save(meteringStrategy: MeteringStrategy) {
        when (meteringStrategy) {
            is DayOfWeekMeteringStrategy -> {
                return transaction {
                    MeteringStrategyEntity
                        .insertAndGetId {
                            it[lotId] = meteringStrategy.lotId
                            it[dayOfWeek] = meteringStrategy.dayOfWeek
                            it[strategyType] = DAY_OF_WEEK
                            it[effectiveDate] = meteringStrategy.effectiveDate
                            it[data] = meteringStrategy.timeSlotMeter.toString()
                            it[registeredAt] = LocalDateTime.now()
                            it[registeredBy] = "system"
                        }
                }
            }

            is SpecifiedDateMeteringStrategy -> {
                return transaction {
                    MeteringStrategyEntity
                        .insertAndGetId {
                            it[lotId] = meteringStrategy.lotId
                            it[dayOfWeek] = null
                            it[strategyType] = SPECIFIED_DATE
                            it[effectiveDate] = null
                            it[data] = meteringStrategy.timeSlotMeter.toString()
                            it[registeredAt] = LocalDateTime.now()
                            it[registeredBy] = "system"
                        }
                }
            }
        }
    }

    fun findByLotId(lotId: Long): List<MeteringStrategy> {
        TODO()
    }

    fun getDayOfWeekMeteringStrategies(): List<DayOfWeekMeteringStrategy> {
        TODO()
    }

    fun getSpecifiedDayMeteringStrategies(): List<SpecifiedDateMeteringStrategy> {
        TODO()
    }
}
