package me.choicore.samples.operation

import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS

class TimeSlotTests {
    fun calculateEffectiveDate(
        registrationDate: LocalDate,
        targetDayOfWeek: DayOfWeek,
    ): LocalDate {
        // 등록일의 요일
        val registrationDayOfWeek = registrationDate.dayOfWeek

        // 이번 주 목표 요일의 날짜 계산
        val daysToAdd =
            (targetDayOfWeek.value - registrationDayOfWeek.value).let { diff ->
                when {
                    diff > 0 -> diff // 목표 요일이 등록일 이후인 경우
                    diff == 0 -> 0 // 등록일과 목표 요일이 같은 경우
                    else -> diff + 7 // 목표 요일이 등록일 이전인 경우 (다음 주)
                }
            }

        // 이번 주 목표 요일의 날짜
        val thisWeekTargetDate = registrationDate.plusDays(daysToAdd.toLong())

        // 등록일과 같은 날이라면 오늘부터 적용, 아니면 계산된 날짜 적용
        return if (daysToAdd == 0) registrationDate else thisWeekTargetDate
    }

//    @Test
//    @DisplayName("같은 요일의 일정을 수정하려면 새로운 일정을 추가하고 시행 일자가 등록일 기준으로 지났으면 다음주부터 적용하게 한다")
//    fun t2() {
//        val currentSchedule =
//            DayOfWeekOperatingSchedule(
//                id = 1,
//                lotId = 1,
//                dayOfWeek = DayOfWeek.MONDAY,
//                rates =
//                    listOf(
//                        TimeBasedRate(
//                            timeSlot = TimeSlot(startTimeInclusive = "10:00", endTimeExclusive = "12:00:00"),
//                            rate = 1000,
//                        ),
//                        TimeBasedRate(
//                            timeSlot = TimeSlot(startTimeInclusive = "12:00", endTimeExclusive = "15:00:00"),
//                            rate = 2000,
//                        ),
//                    ),
//                effectiveDate = LocalDate.of(2025, 3, 3),
//            )
//
//        val nextSchedule =
//            DayOfWeekOperatingSchedule(
//                id = 1,
//                lotId = 1,
//                dayOfWeek = DayOfWeek.MONDAY,
//                rates =
//                    listOf(
//                        TimeBasedRate(
//                            timeSlot = TimeSlot(startTimeInclusive = "10:00", endTimeExclusive = "12:00:00"),
//                            rate = 1000,
//                        ),
//                        TimeBasedRate(
//                            timeSlot = TimeSlot(startTimeInclusive = "12:00", endTimeExclusive = "15:00:00"),
//                            rate = 2000,
//                        ),
//                    ),
//                effectiveDate = LocalDate.of(2025, 3, 10),
//            )
//    }

    @Test
    fun t1() {
        val timeSlot1 = TimeSlot(startTimeInclusive = "10:00", endTimeExclusive = "12:00:00")
        val timeSlot2 = TimeSlot(startTimeInclusive = "12:00", endTimeExclusive = "15:00:00")

        val timeSlots = listOf(timeSlot1, timeSlot2)

        val startDateTime: LocalDateTime = LocalDateTime.of(LocalDate.of(2025, 3, 1), LocalTime.of(11, 0))
        val endDateTime: LocalDateTime = LocalDateTime.of(LocalDate.of(2025, 3, 5), LocalTime.of(14, 0))

        val start = startDateTime.toLocalDate()
        val end = endDateTime.toLocalDate()

        val intersections: MutableList<Intersection> = mutableListOf()
        when (val between: Long = DAYS.between(start, end)) {
            0L -> {
                intersections += charge(start, startDateTime.toLocalTime(), endDateTime.toLocalTime(), timeSlots)
            }

            1L -> {
                intersections += charge(start, startDateTime.toLocalTime(), LocalTime.MIDNIGHT, timeSlots)
                intersections += charge(end, LocalTime.MIDNIGHT, endDateTime.toLocalTime(), timeSlots)
            }

            else -> {
                intersections += charge(start, startDateTime.toLocalTime(), endDateTime.toLocalTime(), timeSlots)
                (1 until between)
                    .forEach {
                        intersections +=
                            charge(
                                start.plusDays(it),
                                LocalTime.MIDNIGHT,
                                LocalTime.MIDNIGHT,
                                timeSlots,
                            )
                    }
                intersections += charge(end, LocalTime.MIDNIGHT, endDateTime.toLocalTime(), timeSlots)
            }
        }

        for (intersection in intersections) {
            println(intersection)
        }
    }

    data class Intersection(
        val source: TimeSlot,
        val result: TimeSlot,
    )

    private fun charge(
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        timeSlots: List<TimeSlot>,
    ): List<Intersection> {
        println("date: $date, startTime: $startTime, endTime: $endTime")

        return timeSlots
            .mapNotNull { timeSlot ->
                timeSlot.intersect(startTime, endTime)?.let { intersection ->
                    Intersection(timeSlot, intersection)
                }
            }
    }
}
