package me.choicore.samples.operation.infrastructure.exposed

import me.choicore.samples.operation.domain.DayOfWeekMeteringStrategy
import me.choicore.samples.operation.domain.TimeSlot
import me.choicore.samples.operation.domain.TimeSlotMeasurer
import me.choicore.samples.operation.domain.TimeSlotMeter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate

@SpringBootTest
class MeteringStrategyRepositoryTests(
    @Autowired
    private val meteringStrategyRepository: MeteringStrategyRepositoryImpl,
) {
    @Test
    fun t1() {
        val dayOfWeekMeteringStrategy =
            DayOfWeekMeteringStrategy(
                lotId = 1,
                dayOfWeek = MONDAY,
                timeSlotMeter =
                    TimeSlotMeter(
                        TimeSlotMeasurer(TimeSlot("00:00", "09:30"), 1.0),
                    ),
                effectiveDate = LocalDate.now(),
            )

        val saved: Long = meteringStrategyRepository.save(dayOfWeekMeteringStrategy)
        assertThat(saved).isGreaterThan(0)
    }

    @Test
    fun t2() {
        val strategies = meteringStrategyRepository.findByLotId(1)
        for (strategy in strategies) {
            println(strategy)
        }
    }
}
