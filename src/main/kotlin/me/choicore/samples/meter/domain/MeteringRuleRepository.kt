package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import java.time.LocalDate

interface MeteringRuleRepository {
    fun save(meteringRule: MeteringRule): MeteringRule

    fun findBy(
        lotId: SecondaryKey,
        meteringMode: MeteringMode,
        effectiveDate: LocalDate,
    ): List<MeteringRule>

    fun existsBy(
        lotId: SecondaryKey,
        effectiveDate: LocalDate,
    ): Boolean

    fun deleteById(id: PrimaryKey)

    fun getAvailableTimeBasedMeteringRule(
        lotId: SecondaryKey,
        measureOn: LocalDate,
    ): MeteringRule?
}
