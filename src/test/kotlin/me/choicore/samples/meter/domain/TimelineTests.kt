package me.choicore.samples.meter.domain

import me.choicore.samples.meter.domain.dsl.Timeline
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalTime

class TimelineTests {
    @Test
    @DisplayName("Timeline DSL test")
    fun t1() {
        assertThatNoException().isThrownBy {
            Timeline {
                slot(LocalTime.of(0, 0), LocalTime.of(9, 0))
                slot(LocalTime.of(12, 0), LocalTime.of(13, 0))
                slot(startTimeInclusive = "09:00", endTimeExclusive = "12:00:00")
                slot(startTimeInclusive = "13:00", endTimeExclusive = "17:00:00")
                slot(LocalTime.of(17, 0), LocalTime.of(23, 0))
                slot(startTimeInclusive = "23:00", endTimeExclusive = "00:00:00")
            }
        }
    }
}
