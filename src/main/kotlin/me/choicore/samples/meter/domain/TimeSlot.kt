package me.choicore.samples.meter.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalTime

data class TimeSlot(
    val startTimeInclusive: LocalTime,
    val endTimeExclusive: LocalTime,
) {
    init {
        if (this.endTimeExclusive != LocalTime.MIDNIGHT && this.startTimeInclusive >= this.endTimeExclusive) {
            throw IllegalArgumentException("시작 시간은 종료 시간보다 이른 시간이어야 합니다.")
        }
    }

    constructor(startTimeInclusive: String, endTimeExclusive: String) : this(
        try {
            LocalTime.parse(startTimeInclusive)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid start time format: $startTimeInclusive", e)
        },
        try {
            LocalTime.parse(endTimeExclusive)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid end time format: $endTimeExclusive", e)
        },
    )

    private val allDay: Boolean =
        this.startTimeInclusive == this.endTimeExclusive && this.endTimeExclusive == LocalTime.MIDNIGHT

    val duration: Duration =
        when {
            this.allDay -> TWENTY_FOR_HOURS_DURATION
            else -> {
                val between: Duration = Duration.between(this.startTimeInclusive, this.endTimeExclusive)
                if (between.isNegative || between.isZero && this.endTimeExclusive == LocalTime.MIDNIGHT) {
                    between.plus(TWENTY_FOR_HOURS_DURATION)
                } else {
                    between
                }
            }
        }

    fun intersect(
        startTimeInclusive: LocalTime,
        endTimeExclusive: LocalTime,
    ): TimeSlot? {
        val initialized: TimeSlot =
            try {
                TimeSlot(startTimeInclusive, endTimeExclusive)
            } catch (e: IllegalArgumentException) {
                log.warn("Failed to initialize TimeSlot: {}", e.message, e)
                return null
            }

        if (!this.overlaps(initialized)) {
            return null
        }

        if (this.allDay) {
            return initialized
        }

        val from: LocalTime = maxOf(this.startTimeInclusive, startTimeInclusive)
        val until: LocalTime =
            when {
                this.endTimeExclusive == LocalTime.MIDNIGHT && endTimeExclusive == LocalTime.MIDNIGHT -> {
                    LocalTime.MIDNIGHT
                }

                this.endTimeExclusive == LocalTime.MIDNIGHT -> {
                    endTimeExclusive
                }

                endTimeExclusive == LocalTime.MIDNIGHT -> {
                    this.endTimeExclusive
                }

                else -> {
                    minOf(this.endTimeExclusive, endTimeExclusive)
                }
            }

        return TimeSlot(startTimeInclusive = from, endTimeExclusive = until)
    }

    fun overlaps(other: TimeSlot): Boolean =
        when {
            this.allDay || other.allDay -> true
            this.endTimeExclusive == LocalTime.MIDNIGHT && other.startTimeInclusive == LocalTime.MIDNIGHT -> false
            this.startTimeInclusive == LocalTime.MIDNIGHT && other.endTimeExclusive == LocalTime.MIDNIGHT -> false
            else -> this.startTimeInclusive < other.endTimeExclusive && other.startTimeInclusive < this.endTimeExclusive
        }

    operator fun contains(value: LocalTime): Boolean = value >= startTimeInclusive && value < endTimeExclusive

    companion object {
        val log: Logger = LoggerFactory.getLogger(TimeSlot::class.java)
        val TWENTY_FOR_HOURS_DURATION: Duration = Duration.ofHours(24)
        val ALL_DAY = TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
    }
}
