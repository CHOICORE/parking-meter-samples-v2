package me.choicore.samples.operation.domain

import me.choicore.samples.operation.domain.dsl.Timeline

object TimeSlotMeasurerFactory {
    fun fullest(source: List<TimeSlotMeasurer>): List<TimeSlotMeasurer> {
        if (source.isEmpty()) {
            return listOf(TimeSlotMeasurer.STANDARD)
        }
        val additional: List<TimeSlotMeasurer> =
            Timeline { source.forEach { slot(it.timeSlot) } }
                .run { this.unset.map { TimeSlotMeasurer.standard(it) } }

        return (source + additional)
            .sortedBy { it.timeSlot.startTimeInclusive }
    }
}
