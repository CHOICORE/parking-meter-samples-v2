package me.choicore.samples.operation

import me.choicore.samples.meter.TimelineMeter
import me.choicore.samples.operation.OperatingSchedule.DayOfWeekOperatingSchedule
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDate

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OperatingScheduleRepositoryImplTests(
    private val operatingScheduleRepository: OperatingScheduleRepository,
) {
    @Test
    fun save() {
        val dayOfWeekOperatingSchedule =
            DayOfWeekOperatingSchedule(
                lotId = 1,
                effectiveDate = LocalDate.now(),
                timelineMeter = TimelineMeter.STANDARD,
            )

        val save = operatingScheduleRepository.save(dayOfWeekOperatingSchedule)
    }
}
