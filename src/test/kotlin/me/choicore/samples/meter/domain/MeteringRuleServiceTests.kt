package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.MeteringMode.REPEAT
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleTable
import me.choicore.samples.meter.infrastructure.persistence.exposed.table.MeteringRuleTable.Entity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@SpringBootTest
@TestConstructor(autowireMode = ALL)
@Transactional
class MeteringRuleServiceTests(
    private val meteringRuleService: MeteringRuleService,
) {
    @ParameterizedTest
    @EnumSource(DayOfWeek::class)
    fun t1(dayOfWeek: DayOfWeek) {
        val effectiveDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))

        meteringRuleService.register(
            lotId = 1L,
            meteringMode = REPEAT,
            effectiveDate = effectiveDate.minusWeeks(2),
            measurers =
                listOf(
                    TimeSlotMeasurer(
                        timeSlot = TimeSlot(startTimeInclusive = "09:00", endTimeExclusive = "18:00"),
                        calibration = Calibration.VOID,
                    ),
                ),
        )

        meteringRuleService.register(
            lotId = 1L,
            meteringMode = REPEAT,
            effectiveDate = effectiveDate.minusWeeks(1),
            measurers =
                listOf(
                    TimeSlotMeasurer(
                        timeSlot = TimeSlot(startTimeInclusive = "09:00", endTimeExclusive = "18:00"),
                        calibration = Calibration.VOID,
                    ),
                ),
        )

        meteringRuleService.register(
            lotId = 1L,
            meteringMode = REPEAT,
            effectiveDate = effectiveDate,
            measurers =
                listOf(
                    TimeSlotMeasurer(
                        timeSlot = TimeSlot(startTimeInclusive = "09:00", endTimeExclusive = "18:00"),
                        calibration = Calibration.VOID,
                    ),
                ),
        )
    }

    @Test
    @DisplayName("규칙을 비활성화한다.")
    fun t2() {
        val registered =
            meteringRuleService.register(
                lotId = 2L,
                meteringMode = REPEAT,
                effectiveDate = LocalDate.now(),
                measurers =
                    listOf(
                        TimeSlotMeasurer(
                            timeSlot = TimeSlot(startTimeInclusive = "09:00", endTimeExclusive = "18:00"),
                            calibration = Calibration.VOID,
                        ),
                    ),
            )
        meteringRuleService.unregister(id = registered)
        val entity: Entity = Entity.find { MeteringRuleTable.id eq registered }.single()
        println(entity)
    }
}
