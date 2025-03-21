package me.choicore.samples.operation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.meter.TimeSlotMeasurer
import me.choicore.samples.meter.TimelineMeter
import me.choicore.samples.operation.OperatingSchedule.RepeatMode
import me.choicore.samples.support.exposed.AuditableLongIdTable
import me.choicore.samples.support.jackson.ObjectMappers
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.json.json
import java.time.DayOfWeek
import java.time.LocalDate

object OperatingScheduleTable :
    AuditableLongIdTable(
        name = "operating_schedule",
        columnName = "schedule_id",
    ) {
    val lotId: Column<Long> = long("lot_id")
    val mode: Column<RepeatMode> = enumerationByName("mode", 14, RepeatMode::class)
    val dayOfWeek: Column<DayOfWeek?> = enumerationByName("day_of_week", 9, DayOfWeek::class).nullable()
    val effectiveDate: Column<LocalDate> = date("effective_date")
    val timelineMeter: Column<TimelineMeter> =
        json(
            "timeline_meter",
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
}
