package me.choicore.samples.operation.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimeSlotMeasurerFactoryTests {
    @Test
    fun t1() {
        val timeSlotMeasurer1 = TimeSlotMeasurer(TimeSlot("00:00", "09:30"), 1.0)
        val timeSlotMeasurer2 = TimeSlotMeasurer(TimeSlot("13:00", "15:30"), 1.0)

        val fullest: List<TimeSlotMeasurer> =
            TimeSlotMeasurerFactory.fullest(listOf(timeSlotMeasurer1, timeSlotMeasurer2))

        assertThat(fullest).hasSize(4)
    }
}
