package me.choicore.samples.operation

import me.choicore.samples.operation.OperatingSchedule.DayOfWeekOperatingSchedule
import me.choicore.samples.operation.OperatingSchedule.RepeatMode
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.ONCE
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.REPEAT
import me.choicore.samples.operation.OperatingSchedule.SpecifiedDateOperatingSchedule
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class OperatingScheduleRepositoryImpl : OperatingScheduleRepository {
    @Transactional
    override fun save(operatingSchedule: OperatingSchedule): OperatingSchedule {
        val id =
            when (operatingSchedule) {
                is DayOfWeekOperatingSchedule -> {
                    dayOfWeekOperatingSchedule(operatingSchedule)
                }

                is SpecifiedDateOperatingSchedule -> {
                    specifiedDateOperatingSchedule(operatingSchedule)
                }
            }
        return id
    }

    @Transactional(readOnly = true)
    override fun findBy(
        lotId: Long,
        effectiveDate: LocalDate,
        vararg mode: RepeatMode,
    ): List<OperatingSchedule> =
        OperatingScheduleTable
            .selectAll()
            .where {
                (OperatingScheduleTable.lotId eq lotId) and
                    (OperatingScheduleTable.effectiveDate eq effectiveDate) and
                    (OperatingScheduleTable.mode inList mode.toSet()) and (OperatingScheduleTable.deletedAt.isNull())
            }.map { it.toOperatingSchedule() }

    private fun dayOfWeekOperatingSchedule(schedule: DayOfWeekOperatingSchedule): DayOfWeekOperatingSchedule {
        val id =
            OperatingScheduleTable
                .insertAndGetId {
                    it[lotId] = schedule.lotId
                    it[mode] = schedule.mode
                    it[dayOfWeek] = schedule.dayOfWeek
                    it[effectiveDate] = schedule.effectiveDate
                    it[timelineMeter] = schedule.timelineMeter
                    it[registeredAt] = schedule.registeredAt
                    it[registeredBy] = schedule.registeredBy
                }.value
        return schedule.copy(id = id)
    }

    private fun specifiedDateOperatingSchedule(schedule: SpecifiedDateOperatingSchedule): SpecifiedDateOperatingSchedule {
        val id =
            OperatingScheduleTable
                .insertAndGetId {
                    it[lotId] = schedule.lotId
                    it[mode] = schedule.mode
                    it[effectiveDate] = schedule.effectiveDate
                    it[timelineMeter] = schedule.timelineMeter
                    it[registeredAt] = schedule.registeredAt
                    it[registeredBy] = schedule.registeredBy
                }.value
        return schedule.copy(id = id)
    }

    private fun ResultRow.toOperatingSchedule(): OperatingSchedule =
        when (this[OperatingScheduleTable.mode]) {
            REPEAT -> {
                DayOfWeekOperatingSchedule(
                    id = this[OperatingScheduleTable.id].value,
                    lotId = this[OperatingScheduleTable.lotId],
                    effectiveDate = this[OperatingScheduleTable.effectiveDate],
                    timelineMeter = this[OperatingScheduleTable.timelineMeter],
                    registeredAt = this[OperatingScheduleTable.registeredAt],
                    registeredBy = this[OperatingScheduleTable.registeredBy],
                )
            }

            ONCE -> {
                SpecifiedDateOperatingSchedule(
                    id = this[OperatingScheduleTable.id].value,
                    lotId = this[OperatingScheduleTable.lotId],
                    effectiveDate = this[OperatingScheduleTable.effectiveDate],
                    timelineMeter = this[OperatingScheduleTable.timelineMeter],
                    registeredAt = this[OperatingScheduleTable.registeredAt],
                    registeredBy = this[OperatingScheduleTable.registeredBy],
                )
            }
        }
}
