package me.choicore.samples.meter.domain.deprecated

import me.choicore.samples.meter.domain.Metric
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Deprecated("unused")
class TimeSeriesMeterTests {
    @Test
    @Disabled
    fun t1() {
        val timelineMeteringStrategyRegistry =
            TimeBasedMeteringStrategyRegistry(emptyList(), emptyList())
        val timeSeriesMeter = TimeSeriesMeter(timelineMeteringStrategyRegistry)
        val metrics: List<Metric> = timeSeriesMeter.measure(LocalDateTime.now(), LocalDateTime.now().plusDays(3))
        metrics.forEach(::println)
    }
}
