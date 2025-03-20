package me.choicore.samples.operation.meter.domain

import me.choicore.samples.operation.meter.domain.TimelineMeteringStrategy.SpecifiedDateMeteringStrategy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class TimelineMeteringStrategyTests {
    @Test
    fun t1() {
        val timelineMeter =
            TimelineMeter(
                TimeSlotMeasurer(
                    timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                    weight = 1.0,
                ),
            )
        val measureOn = LocalDate.of(2025, 3, 20)

        val strategy =
            SpecifiedDateMeteringStrategy(
                timelineMeter = timelineMeter,
                specifiedDate = measureOn,
            )

        // 지정된 날짜에는 적용되어야 함
        assertThat(strategy.applies(measureOn)).isTrue()

        // 다른 날짜에는 적용되지 않아야 함
        assertThat(strategy.applies(measureOn.plusDays(1))).isFalse()
        assertThat(strategy.applies(measureOn.minusDays(1))).isFalse()

        val measurand =
            Measurand(
                measureOn = measureOn,
                from = LocalTime.of(9, 0),
                to = LocalTime.of(10, 0),
            )

        val metrics = strategy.measure(measurand)
        assertThat(metrics).hasSize(1)
        metrics.forEach(::println)
    }
}
