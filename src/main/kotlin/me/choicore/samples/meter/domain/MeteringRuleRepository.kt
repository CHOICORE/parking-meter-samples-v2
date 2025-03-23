package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import java.time.LocalDate

interface MeteringRuleRepository : MeteringStrategyRegistry {
    fun save(meteringRule: MeteringRule): MeteringRule

    fun findBy(
        lotId: ForeignKey,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
    ): List<MeteringRule>

    fun existsBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
    ): Boolean

    fun deleteById(id: PrimaryKey)
}
