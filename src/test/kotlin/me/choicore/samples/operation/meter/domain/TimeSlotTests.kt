package me.choicore.samples.operation.meter.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalTime

class TimeSlotTests {
    @Test
    @DisplayName("시간대가 겹치는지 확인")
    fun t6() {
        val timeSlot1 = TimeSlot(LocalTime.of(23, 0), LocalTime.MIDNIGHT)
        val timeSlot2 = TimeSlot(LocalTime.MIDNIGHT, LocalTime.of(23, 0))
        assertThat(timeSlot1.overlaps(timeSlot2)).isFalse
        assertThat(timeSlot2.overlaps(timeSlot1)).isFalse
    }

    @Test
    @DisplayName("자정을 제외하고 시작 시간은 종료 시간보다 항상 이른 시간이어야 한다.")
    fun t7() {
        assertThatNoException().isThrownBy {
            val startTimeInclusive: LocalTime = LocalTime.of(20, 0, 0)
            TimeSlot(startTimeInclusive, LocalTime.MIDNIGHT)
            TimeSlot(startTimeInclusive, LocalTime.MIN)
        }
        assertThatThrownBy {
            val startTimeInclusive: LocalTime = LocalTime.of(20, 0, 0)
            TimeSlot(startTimeInclusive, startTimeInclusive)
            TimeSlot(startTimeInclusive, startTimeInclusive.minusNanos(1))
        }
    }

    @Test
    @DisplayName("일반적인 시간대 겹침 확인")
    fun testNormalOverlaps() {
        // 완전히 겹치는 경우
        val slot1 = TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
        val slot2 = TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
        assertThat(slot1.overlaps(slot2)).isTrue()

        // 부분적으로 겹치는 경우
        val slot3 = TimeSlot(LocalTime.of(11, 0), LocalTime.of(13, 0))
        assertThat(slot1.overlaps(slot3)).isTrue()
        assertThat(slot3.overlaps(slot1)).isTrue()

        // 경계에서 겹치는 경우
        val slot4 = TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
        assertThat(slot1.overlaps(slot4)).isFalse()
        assertThat(slot4.overlaps(slot1)).isFalse()

        // 포함관계인 경우
        val slot5 = TimeSlot(LocalTime.of(10, 30), LocalTime.of(11, 30))
        assertThat(slot1.overlaps(slot5)).isTrue()
        assertThat(slot5.overlaps(slot1)).isTrue()

        // 완전히 분리된 경우
        val slot6 = TimeSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
        assertThat(slot1.overlaps(slot6)).isFalse()
        assertThat(slot6.overlaps(slot1)).isFalse()
    }

    @Test
    @DisplayName("자정 관련 시간대 겹침 확인")
    fun testMidnightOverlaps() {
        // 자정에서 끝나는 시간대
        val endAtMidnight = TimeSlot(LocalTime.of(22, 0), LocalTime.MIDNIGHT)

        // 자정에서 시작하는 시간대
        val startAtMidnight = TimeSlot(LocalTime.MIDNIGHT, LocalTime.of(2, 0))

        // 서로 겹치지 않아야 함
        assertThat(endAtMidnight.overlaps(startAtMidnight)).isFalse()
        assertThat(startAtMidnight.overlaps(endAtMidnight)).isFalse()
    }

    @Test
    @DisplayName("전체 하루 시간대 겹침 확인")
    fun testAllDayOverlaps() {
        // 전체 하루 시간대
        val allDay = TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)

        // 일반 시간대
        val normal = TimeSlot(LocalTime.of(10, 0), LocalTime.of(14, 0))

        // 자정에 끝나는 시간대
        val endAtMidnight = TimeSlot(LocalTime.of(22, 0), LocalTime.MIDNIGHT)

        // 자정에 시작하는 시간대
        val startAtMidnight = TimeSlot(LocalTime.MIDNIGHT, LocalTime.of(2, 0))

        // 모든 시간대와 겹쳐야 함
        assertThat(allDay.overlaps(normal)).isTrue()
        assertThat(normal.overlaps(allDay)).isTrue()
        assertThat(allDay.overlaps(endAtMidnight)).isTrue()
        assertThat(endAtMidnight.overlaps(allDay)).isTrue()
        assertThat(allDay.overlaps(startAtMidnight)).isTrue()
        assertThat(startAtMidnight.overlaps(allDay)).isTrue()

        // 전체 하루 시간대끼리도 겹쳐야 함
        val anotherAllDay = TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
        assertThat(allDay.overlaps(anotherAllDay)).isTrue()
    }

    @Test
    @DisplayName("Duration 계산 확인")
    fun testDurationCalculation() {
        // 일반적인 시간대 기간
        val normal = TimeSlot(LocalTime.of(10, 0), LocalTime.of(14, 0))
        assertThat(normal.duration).isEqualTo(Duration.ofHours(4))

        // 자정에 끝나는 시간대 기간
        val endAtMidnight = TimeSlot(LocalTime.of(22, 0), LocalTime.MIDNIGHT)
        assertThat(endAtMidnight.duration).isEqualTo(Duration.ofHours(2))

        // 전체 하루 시간대 기간
        val allDay = TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
        assertThat(allDay.duration).isEqualTo(Duration.ofHours(24))

        // 1분 시간대 기간
        val oneMinute = TimeSlot(LocalTime.of(12, 0), LocalTime.of(12, 1))
        assertThat(oneMinute.duration).isEqualTo(Duration.ofMinutes(1))
    }

    @Test
    @DisplayName("유효하지 않은 시간대 초기화")
    fun testInvalidInitialization() {
        // 시작 시간이 종료 시간보다 늦은 경우
        assertThatThrownBy {
            TimeSlot(LocalTime.of(14, 0), LocalTime.of(10, 0))
        }.isInstanceOf(IllegalArgumentException::class.java)

        // 시작 시간과 종료 시간이 같은 경우 (자정 제외)
        assertThatThrownBy {
            TimeSlot(LocalTime.of(15, 0), LocalTime.of(15, 0))
        }.isInstanceOf(IllegalArgumentException::class.java)

        // 자정이 아닌 시간의 경우 시작이 종료보다 늦으면 안됨
        assertThatThrownBy {
            TimeSlot(LocalTime.of(12, 0, 1), LocalTime.of(12, 0, 0))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("자정 특수 케이스 초기화 테스트")
    fun testMidnightInitialization() {
        assertThatNoException().isThrownBy {
            TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
            TimeSlot(LocalTime.MIN, LocalTime.MIN)
            TimeSlot(LocalTime.of(0, 0, 0), LocalTime.of(0, 0))
        }
    }

    @Test
    @DisplayName("intersect(메서드 테스트 - 경계 케이스")
    fun testIntersectionEdgeCases() {
        // 10:00 ~ 14:00 시간대
        val timeSlot = TimeSlot(LocalTime.of(10, 0), LocalTime.of(14, 0))

        // 정확히 같은 시간대
        val intersect1 = timeSlot.intersect(LocalTime.of(10, 0), LocalTime.of(14, 0))
        assertThat(intersect1).isNotNull
        assertThat(intersect1?.startTimeInclusive).isEqualTo(LocalTime.of(10, 0))
        assertThat(intersect1?.endTimeExclusive).isEqualTo(LocalTime.of(14, 0))

        // 시작 시간만 겹치는 경우 - 시작 시간이 같은 케이스
        val intersect2 = timeSlot.intersect(LocalTime.of(10, 0), LocalTime.of(10, 1))
        assertThat(intersect2).isNotNull
        assertThat(intersect2?.startTimeInclusive).isEqualTo(LocalTime.of(10, 0))
        assertThat(intersect2?.endTimeExclusive).isEqualTo(LocalTime.of(10, 1))

        // 종료 시간만 겹치는 경우 - 종료 시간이 같은 케이스
        val intersect3 = timeSlot.intersect(LocalTime.of(13, 59), LocalTime.of(14, 0))
        assertThat(intersect3).isNotNull
        assertThat(intersect3?.startTimeInclusive).isEqualTo(LocalTime.of(13, 59))
        assertThat(intersect3?.endTimeExclusive).isEqualTo(LocalTime.of(14, 0))
    }
}
