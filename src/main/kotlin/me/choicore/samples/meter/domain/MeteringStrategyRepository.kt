package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringStrategy.TimeSlotMeteringStrategy

interface MeteringStrategyRepository<T : TimeSlotMeteringStrategy> {
    val supported: Class<T>

    fun save(meteringStrategy: T): T

    fun findByLotId(lotId: ForeignKey): List<T>
}
