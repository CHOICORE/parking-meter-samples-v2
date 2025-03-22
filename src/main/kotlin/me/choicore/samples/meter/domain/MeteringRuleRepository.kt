package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import java.time.LocalDate

interface MeteringRuleRepository {
    fun save(meteringRule: MeteringRule): MeteringRule

    fun deleteById(id: PrimaryKey)

    fun findBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
        vararg meteringMode: MeteringMode,
    ): List<MeteringRule>
}
