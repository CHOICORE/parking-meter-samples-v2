package me.choicore.samples.operation.domain

import me.choicore.samples.operation.context.entity.ForeignKey
import me.choicore.samples.operation.context.entity.PrimaryKey

interface MeteringStrategyRepository {
    fun save(meteringStrategy: MeteringStrategy): PrimaryKey

    fun findByLotId(lotId: ForeignKey): List<MeteringStrategy>
}
