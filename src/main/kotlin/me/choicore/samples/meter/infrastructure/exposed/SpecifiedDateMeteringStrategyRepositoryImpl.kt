package me.choicore.samples.meter.infrastructure.exposed

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringStrategyRepository
import me.choicore.samples.meter.domain.MeteringStrategyType
import me.choicore.samples.meter.domain.SpecifiedDateMeteringStrategyEntity
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.deletedAt
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.deletedBy
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.id
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.lastModifiedAt
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.lastModifiedBy
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.registeredAt
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.registeredBy
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.specifiedDate
import me.choicore.samples.meter.infrastructure.exposed.table.MeteringStrategyTable.timeSlotMeter
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SpecifiedDateMeteringStrategyRepositoryImpl : MeteringStrategyRepository<SpecifiedDateMeteringStrategyEntity> {
    override val supported: Class<SpecifiedDateMeteringStrategyEntity> = SpecifiedDateMeteringStrategyEntity::class.java

    @Transactional(readOnly = true)
    override fun findByLotId(lotId: ForeignKey): List<SpecifiedDateMeteringStrategyEntity> =
        MeteringStrategyTable
            .selectAll()
            .where {
                (MeteringStrategyTable.lotId eq lotId.value) and (MeteringStrategyTable.strategyType eq MeteringStrategyType.SPECIFIED_DATE)
            }.map {
                SpecifiedDateMeteringStrategyEntity(
                    id = PrimaryKey(it[id].value),
                    lotId = lotId,
                    timeSlotMeter = it[timeSlotMeter],
                    specifiedDate = it[specifiedDate]!!,
                    registeredAt = it[registeredAt],
                    registeredBy = it[registeredBy],
                    modifiedAt = it[lastModifiedAt],
                    modifiedBy = it[lastModifiedBy],
                    deletedAt = it[deletedAt],
                    deletedBy = it[deletedBy],
                )
            }

    @Transactional
    override fun save(meteringStrategy: SpecifiedDateMeteringStrategyEntity): SpecifiedDateMeteringStrategyEntity {
        val registered: Long =
            MeteringStrategyTable
                .insertAndGetId {
                    it[lotId] = meteringStrategy.lotId.value
                    it[strategyType] = MeteringStrategyType.SPECIFIED_DATE
                    it[timeSlotMeter] = meteringStrategy.timeSlotMeter
                    it[specifiedDate] = meteringStrategy.specifiedDate
                    it[registeredAt] = meteringStrategy.registeredAt
                    it[registeredBy] = meteringStrategy.registeredBy
                }.value
        return meteringStrategy.copy(id = PrimaryKey(registered))
    }
}
