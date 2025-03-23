package me.choicore.samples.meter.infrastructure.persistence.exposed.table

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy.SpecifiedDateBasedMeteringStrategy
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

object MeteringRuleTable :
    AuditableLongIdTable(
        name = "metering_rule",
        columnName = "rule_id",
    ) {
    val lotId: Column<Long> = long("lot_id")
    val meteringMode: Column<MeteringMode> = enumerationByName("mode", 14, MeteringMode::class)
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

    private val objectMapper: ObjectMapper = ObjectMappers.getInstance()

    init {
        uniqueIndex(lotId, effectiveDate, meteringMode, deletedAt)
    }

    class Entity(
        id: EntityID<Long>,
    ) : LongEntity(id = id) {
        companion object : LongEntityClass<Entity>(MeteringRuleTable)

        val lotId by MeteringRuleTable.lotId
        val meteringMode by MeteringRuleTable.meteringMode
        var effectiveDate by MeteringRuleTable.effectiveDate
        var dayOfWeek by MeteringRuleTable.dayOfWeek
        var timelineMeter by MeteringRuleTable.timelineMeter
        val registeredAt by MeteringRuleTable.registeredAt
        val registeredBy by MeteringRuleTable.registeredBy
        var lastModifiedAt by MeteringRuleTable.lastModifiedAt
        var lastModifiedBy by MeteringRuleTable.lastModifiedBy
        var deletedAt by MeteringRuleTable.deletedAt
        var deletedBy by MeteringRuleTable.deletedBy

        fun convert(): MeteringRule =
            MeteringRule(
                id = PrimaryKey(this.id.value),
                lotId = ForeignKey(this.lotId),
                timeBasedMeteringStrategy =
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
