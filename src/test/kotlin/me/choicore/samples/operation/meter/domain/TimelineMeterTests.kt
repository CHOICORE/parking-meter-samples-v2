package me.choicore.samples.operation.meter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TimelineMeterTests {
    @Test
    @DisplayName("타임 슬롯 측정기를 등록하면 빈 시간 측정기들이 추가된다")
    fun t1() {
        val timelineMeter =
            TimelineMeter(
                TimeSlotMeasurer(TimeSlot("00:00", "09:30"), 1.0),
                TimeSlotMeasurer(TimeSlot("13:00", "15:30"), 1.0),
            )
        assertThat(timelineMeter.measurers).hasSize(4)
    }
}
