package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import java.time.LocalDate

interface TimelineMeteringRuleRepository {
    fun save(timelineMeteringRule: TimelineMeteringRule): TimelineMeteringRule

    fun findBy(
        lotId: ForeignKey,
        effectiveDate: LocalDate,
        vararg meteringMode: MeteringMode,
    ): List<TimelineMeteringRule>
}
