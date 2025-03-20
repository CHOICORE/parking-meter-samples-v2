package me.choicore.samples.meter.infrastructure.persistence.exposed

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.DayOfWeekMeteringStrategyEntity
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategyRepository
import me.choicore.samples.meter.domain.MeteringStrategyType.DAY_OF_WEEK
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.deletedAt
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.deletedBy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.effectiveDate
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.id
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.lastModifiedAt
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.lastModifiedBy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.registeredAt
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.registeredBy
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringStrategyTable.timeSlotMeter
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DayOfWeekMeteringStrategyRepositoryImpl : MeteringStrategyRepository<DayOfWeekMeteringStrategyEntity> {
    override val supported: Class<DayOfWeekMeteringStrategyEntity> = DayOfWeekMeteringStrategyEntity::class.java

    @Transactional(readOnly = true)
    override fun findByLotId(lotId: ForeignKey): List<DayOfWeekMeteringStrategyEntity> =
        MeteringStrategyTable
            .selectAll()
            .where {
                (MeteringStrategyTable.lotId eq lotId.value) and
                    (MeteringStrategyTable.strategyType eq DAY_OF_WEEK)
            }.map {
                DayOfWeekMeteringStrategyEntity(
                    id = PrimaryKey(it[id].value),
                    lotId = lotId,
                    strategy =
                        DayOfWeekMeteringStrategy(
                            timeSlotMeter = it[timeSlotMeter],
                            effectiveDate = it[effectiveDate]!!,
                        ),
                    registeredAt = it[registeredAt],
                    registeredBy = it[registeredBy],
                    modifiedAt = it[lastModifiedAt],
                    modifiedBy = it[lastModifiedBy],
                    deletedAt = it[deletedAt],
                    deletedBy = it[deletedBy],
                )
            }

    @Transactional
    override fun save(meteringStrategy: DayOfWeekMeteringStrategyEntity): DayOfWeekMeteringStrategyEntity {
        val registered: Long =
            MeteringStrategyTable
                .insertAndGetId {
                    it[lotId] = meteringStrategy.lotId.value
                    it[strategyType] = DAY_OF_WEEK
                    it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                    it[effectiveDate] = meteringStrategy.strategy.effectiveDate
                    it[dayOfWeek] = meteringStrategy.strategy.dayOfWeek
                    it[registeredAt] = meteringStrategy.registeredAt
                    it[registeredBy] = meteringStrategy.registeredBy
                }.value

        return meteringStrategy.copy(id = PrimaryKey(registered))
    }
}
