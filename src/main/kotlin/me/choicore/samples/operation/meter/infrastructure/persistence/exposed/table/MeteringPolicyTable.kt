// package me.choicore.samples.operation.meter.infrastructure.persistence.exposed.table
//
// import com.fasterxml.jackson.core.type.TypeReference
// import com.fasterxml.jackson.databind.ObjectMapper
// import me.choicore.samples.operation.meter.domain.TimeSlotMeasurer
// import me.choicore.samples.operation.meter.domain.TimelineMeter
// import me.choicore.samples.support.exposed.AuditableLongIdTable
// import me.choicore.samples.support.jackson.ObjectMappers
// import org.jetbrains.exposed.sql.Column
// import org.jetbrains.exposed.sql.javatime.date
// import org.jetbrains.exposed.sql.json.json
// import java.time.DayOfWeek
// import java.time.LocalDate
//
// object MeteringPolicyTable :
//    AuditableLongIdTable(
//        name = "metering_policy",
//        columnName = "policy_id",
//    ) {
//    val lotId: Column<Long> = long("lot_id")
//    val strategyType: Column<MeteringStrategyType> = enumerationByName("strategy_type", 14, MeteringStrategyType::class)
//    val specifiedDate: Column<LocalDate?> = date("specified_date").nullable()
//    val dayOfWeek: Column<DayOfWeek?> = enumerationByName("day_of_week", 9, DayOfWeek::class).nullable()
//    val effectiveDate: Column<LocalDate?> = date("effective_date").nullable()
//    val timelineMeter: Column<TimelineMeter> =
//        json(
//            "timeline_meter",
//            { obj ->
//                objectMapper.writeValueAsString(obj.measurers)
//            },
//            { json ->
//                TimelineMeter(
//                    objectMapper.readValue(
//                        json,
//                        object : TypeReference<List<TimeSlotMeasurer>>() {},
//                    ),
//                )
//            },
//        )
//
//    private val objectMapper: ObjectMapper = ObjectMappers.INSTANCE
//
//    enum class MeteringStrategyType {
//        SPECIFIED_DATE,
//        DAY_OF_WEEK,
//    }
// }
