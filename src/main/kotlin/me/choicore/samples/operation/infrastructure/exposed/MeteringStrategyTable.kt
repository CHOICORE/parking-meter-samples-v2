package me.choicore.samples.operation.infrastructure.exposed

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.operation.domain.MeteringStrategyType
import me.choicore.samples.operation.domain.TimeSlotMeasurer
import me.choicore.samples.operation.domain.TimeSlotMeter
import me.choicore.samples.operation.support.jackson.ObjectMappers
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.json.json
import java.time.DayOfWeek
import java.time.LocalDate

object MeteringStrategyTable :
    AuditableLongIdTable(
        name = "metering_strategy",
        columnName = "strategy_id",
    ) {
    val lotId: Column<Long> = long("lot_id")
    val strategyType: Column<MeteringStrategyType> = enumerationByName("strategy_type", 13, MeteringStrategyType::class)
    val specificDate: Column<LocalDate?> = date("specific_date").nullable()
    val dayOfWeek: Column<DayOfWeek?> = enumerationByName("day_of_week", 9, DayOfWeek::class).nullable()
    val effectiveDate: Column<LocalDate?> = date("effective_date").nullable()
    val timeSlotMeter: Column<TimeSlotMeter> =
        json(
            "time_slot_measurers",
            { obj ->
                this.objectMapper.writeValueAsString(obj.measurers)
            },
            { json ->
                TimeSlotMeter(
                    this.objectMapper.readValue(
                        json,
                        object : TypeReference<List<TimeSlotMeasurer>>() {},
                    ),
                )
            },
        )

    private val objectMapper: ObjectMapper = ObjectMappers.INSTANCE
}
