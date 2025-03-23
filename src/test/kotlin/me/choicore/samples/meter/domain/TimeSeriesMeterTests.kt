package me.choicore.samples.meter.domain

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TimeSeriesMeterTests(
    private val timeSeriesMeter: TimeSeriesMeter,
) {
    @Test
    fun t1() {
        val metrics =
            timeSeriesMeter.measure(
                lotId = 1L,
                startDateTimeInclusive = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                endDateTimeExclusive = LocalDateTime.now(),
            )

        val totalUsage = metrics.sumOf { it.cost }
    }
}
