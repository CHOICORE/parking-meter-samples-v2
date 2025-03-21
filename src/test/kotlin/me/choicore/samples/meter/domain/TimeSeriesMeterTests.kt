package me.choicore.samples.meter.domain

import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TimeSeriesMeterTests {
    @Test
    fun t1() {
        val timelineMeteringStrategyRegistry = TimelineMeteringStrategyRegistry(emptyList(), emptyList())
        val timeSeriesMeter = TimeSeriesMeter(timelineMeteringStrategyRegistry)
        val metrics: List<Metric> = timeSeriesMeter.measure(LocalDateTime.now(), LocalDateTime.now().plusDays(3))
        metrics.forEach(::println)
    }
}
