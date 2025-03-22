package me.choicore.samples.meter.infrastructure.persistence.exposed.table

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.TimeSlotMeasurer
import me.choicore.samples.meter.domain.TimelineMeter
import me.choicore.samples.support.exposed.AuditableLongIdTable
import me.choicore.samples.support.jackson.ObjectMappers
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
}
