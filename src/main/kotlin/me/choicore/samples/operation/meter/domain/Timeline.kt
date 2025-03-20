package me.choicore.samples.operation.meter.domain

import me.choicore.samples.operation.meter.domain.dsl.TimelineDsl
import java.time.LocalTime

class Timeline private constructor(
    val slots: List<TimeSlot>,
) {
    private val _unset: MutableList<TimeSlot> = mutableListOf()
    val unset: List<TimeSlot> get() = this._unset.toList()

    init {
        if (this.slots.isEmpty()) {
            this._unset += TimeSlot.ALL_DAY
        }

        var current: LocalTime = LocalTime.MIDNIGHT

        this.slots.forEach { slot ->
            if (slot.startTimeInclusive > current) {
                this._unset.add(TimeSlot(current, slot.startTimeInclusive))
            }
            current = slot.endTimeExclusive
        }
        if (current != LocalTime.MIDNIGHT) {
            this._unset.add(TimeSlot(current, LocalTime.MIDNIGHT))
        }
    }

    @TimelineDsl
    class Builder {
        private val slots: MutableList<TimeSlot> = mutableListOf()

        fun slot(slot: TimeSlot): Builder {
            val position: Int =
                this.slots
                    .binarySearch {
                        it.startTimeInclusive.compareTo(slot.startTimeInclusive)
                    }.let {
                        if (it < 0) -it - 1 else it
                    }

            if (position > 0) {
                val left: TimeSlot = this.slots[position - 1]
                if (left.overlaps(slot)) {
                    throw IllegalArgumentException("TimeSlot $slot overlaps with existing slot $left")
                }
            }

            if (position < slots.size) {
                val right: TimeSlot = this.slots[position]
                if (right.overlaps(slot)) {
                    throw IllegalArgumentException("TimeSlot $slot overlaps with existing slot $right")
                }
            }

            this.slots.add(position, slot)
            return this
        }

        fun slot(
            startTimeInclusive: LocalTime,
            endTimeExclusive: LocalTime,
        ): Builder {
            this.slot(TimeSlot(startTimeInclusive = startTimeInclusive, endTimeExclusive = endTimeExclusive))
            return this
        }

        fun slot(
            startTimeInclusive: String,
            endTimeExclusive: String,
        ): Builder {
            this.slot(TimeSlot(startTimeInclusive = startTimeInclusive, endTimeExclusive = endTimeExclusive))
            return this
        }

        fun slots(vararg slots: TimeSlot): Builder =
            apply {
                slots(slots.toList())
            }

        private fun slots(slots: List<TimeSlot>): Builder {
            if (slots.isEmpty()) return this

            if (this.slots.isNotEmpty() && slots.size > 1) {
                val sorted: Boolean =
                    slots.zipWithNext().all { (current: TimeSlot, next: TimeSlot) ->
                        current.startTimeInclusive <= next.startTimeInclusive
                    }

                if (sorted) {
                    val result: MutableList<TimeSlot> = mutableListOf()
                    var i = 0
                    var j = 0

                    while (i < this.slots.size && j < slots.size) {
                        val existingTimeSlot: TimeSlot = this.slots[i]
                        val newTimeSlot: TimeSlot = slots[j]

                        if (existingTimeSlot.overlaps(newTimeSlot)) {
                            throw IllegalArgumentException("TimeSlot $newTimeSlot overlaps with existing slot $existingTimeSlot")
                        }

                        if (existingTimeSlot.startTimeInclusive <= newTimeSlot.startTimeInclusive) {
                            result.add(existingTimeSlot)
                            i++
                        } else {
                            result.add(newTimeSlot)
                            j++
                        }
                    }

                    while (i < this.slots.size) result.add(this.slots[i++])
                    while (j < slots.size) result.add(slots[j++])

                    this.slots.clear()
                    this.slots.addAll(result)
                    return this
                }
            }

            slots.forEach { this.slot(it) }
            return this
        }

        fun build(): Timeline = Timeline(this.slots)
    }

    companion object {
        fun builder(): Builder = Builder()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Timeline) return false

        if (this.slots != other.slots) return false
        if (this._unset != other._unset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.slots.hashCode()
        result = 31 * result + this._unset.hashCode()
        return result
    }

    override fun toString(): String = "Timeline(slots=$slots, unset=$unset)"
}
