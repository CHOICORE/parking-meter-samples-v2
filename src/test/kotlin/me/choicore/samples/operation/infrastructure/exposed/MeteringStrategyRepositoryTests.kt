package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.TimeSlotMeter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate

@SpringBootTest
class MeteringStrategyRepositoryTests {
    @Test
    fun t1() {
        val meteringStrategyRepository = MeteringStrategyRepositoryImpl()
        val dayOfWeekMeteringStrategy =
            DayOfWeekMeteringStrategy(
                lotId = 1,
                dayOfWeek = MONDAY,
                timeSlotMeter = TimeSlotMeter(),
                effectiveDate = LocalDate.now(),
            )

        meteringStrategyRepository.save(dayOfWeekMeteringStrategy)
    }
}
