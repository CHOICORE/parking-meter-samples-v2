package me.choicore.samples.meter.domain

import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TimeSeriesMeterTests {
    @Test
    fun t1() {
        val meteringStrategyRegistry = MeteringStrategyRegistry(emptyList(), emptyList())
        val timeSeriesMeter = TimeSeriesMeter(meteringStrategyRegistry)
        val measurements = timeSeriesMeter.measure(LocalDateTime.now(), LocalDateTime.now().plusDays(3))
        measurements.forEach(::println)
    }
}
