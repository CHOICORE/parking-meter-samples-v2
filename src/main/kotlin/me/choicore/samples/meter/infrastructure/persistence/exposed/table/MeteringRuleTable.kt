package me.choicore.samples.meter.infrastructure.persistence.exposed.table

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.SpecifiedDateBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimeSlotMeasurer
import me.choicore.samples.meter.domain.TimelineMeter
import me.choicore.samples.support.exposed.AuditableLongIdTable
import me.choicore.samples.support.jackson.ObjectMappers
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.json.json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

object MeteringRuleTable :
    AuditableLongIdTable(
        name = "metering_rule",
        columnName = "rule_id",
    ) {
    private val objectMapper: ObjectMapper = ObjectMappers.getInstance()

    val lotId: Column<Long> = long("lot_id")
    val meteringMode: Column<MeteringMode> = enumerationByName("metering_mode", 14, MeteringMode::class)
    val dayOfWeek: Column<DayOfWeek?> = enumerationByName("day_of_week", 9, DayOfWeek::class).nullable()
    val effectiveDate: Column<LocalDate> = date("effective_date")
    val timelineMeter: Column<TimelineMeter> =
        json(
            "timeline_meter_json",
            { obj ->
                objectMapper.writeValueAsString(obj.measurers)
            },
            { json ->
                TimelineMeter(
                    objectMapper.readValue(
                        json,
                        object : TypeReference<List<TimeSlotMeasurer>>() {},
                    ),
                )
            },
        )

    init {
        uniqueIndex(this.lotId, this.effectiveDate, this.meteringMode, this.deletedAt)
    }

    class Entity(
        id: EntityID<Long>,
    ) : LongEntity(id = id) {
        companion object : LongEntityClass<Entity>(MeteringRuleTable)

        val lotId: Long by MeteringRuleTable.lotId
        val meteringMode: MeteringMode by MeteringRuleTable.meteringMode
        var effectiveDate: LocalDate by MeteringRuleTable.effectiveDate
        var dayOfWeek: DayOfWeek? by MeteringRuleTable.dayOfWeek
        var timelineMeter: TimelineMeter by MeteringRuleTable.timelineMeter
        val registeredAt: LocalDateTime by MeteringRuleTable.registeredAt
        val registeredBy: String by MeteringRuleTable.registeredBy
        var lastModifiedAt: LocalDateTime? by MeteringRuleTable.lastModifiedAt
        var lastModifiedBy: String? by MeteringRuleTable.lastModifiedBy
        var deletedAt: LocalDateTime? by MeteringRuleTable.deletedAt
        var deletedBy: String? by MeteringRuleTable.deletedBy

        fun convert(): MeteringRule =
            MeteringRule(
                id = PrimaryKey(this.id.value),
                lotId = SecondaryKey(this.lotId),
                meteringStrategy =
                    when (this.meteringMode) {
                        REPEAT -> {
                            DayOfWeekBasedMeteringStrategy(
                                effectiveDate = this.effectiveDate,
                                timelineMeter = this.timelineMeter,
                            )
                        }

                        ONCE ->
                            SpecifiedDateBasedMeteringStrategy(
                                effectiveDate = this.effectiveDate,
                                timelineMeter = this.timelineMeter,
                            )
                    },
                registeredAt = this.registeredAt,
                registeredBy = this.registeredBy,
            ).also {
                it.modifiedAt = this.lastModifiedAt
                it.modifiedBy = this.lastModifiedBy
                it.deletedAt = this.deletedAt
                it.deletedBy = this.deletedBy
            }
    }
}
