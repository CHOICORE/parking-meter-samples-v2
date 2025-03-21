package me.choicore.samples.operation

import me.choicore.samples.meter.TimeSlot
import me.choicore.samples.meter.TimeSlotMeasurer
import me.choicore.samples.operation.OperatingSchedule.RepeatMode.ONCE
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDate

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OperatingScheduleServiceTests(
    private val operatingScheduleService: OperatingScheduleService,
) {
    @Test
    fun t1() {
        operatingScheduleService.register(
            lotId = 1,
            mode = ONCE,
            effectiveDate = LocalDate.now().plusDays(1),
            measurers = listOf(TimeSlotMeasurer(timeSlot = TimeSlot("09:00", "18:00"), 0.0)),
        )
    }
}
