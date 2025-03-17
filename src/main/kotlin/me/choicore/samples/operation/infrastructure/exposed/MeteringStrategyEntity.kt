package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.MeteringStrategyType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import java.time.DayOfWeek
import java.time.LocalDate

object MeteringStrategyEntity : AuditableLongIdTable(
    name = "metering_strategy",
    columnName = "strategy_id",
) {
    val lotId: Column<Long> = long("lot_id")
    val strategyType: Column<MeteringStrategyType> = enumerationByName("type", 13, MeteringStrategyType::class)
    val specificDate: Column<LocalDate?> = date("specific_date").nullable()
    val dayOfWeek: Column<DayOfWeek?> = enumerationByName("day_of_week", 9, DayOfWeek::class).nullable()
    val effectiveDate: Column<LocalDate?> = date("effective_date").nullable()
    val data: Column<String> = text("data")
}
