package me.choicore.samples.meter.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CalibrationTests {
    @Test
    @DisplayName("기본 계수는 1이며, 유효 범위는 0 이상 10 이하이다.")
    fun t1() {
        assertThatNoException().isThrownBy {
            val calibration = Calibration(1.0)
            assertThat(calibration).isEqualTo(Calibration.IDENTITY)
        }
        assertThatNoException().isThrownBy { Calibration(0.0) }
        assertThatNoException().isThrownBy { Calibration(10.0) }
    }

    @Test
    @DisplayName("Calibration 객체 간 동등성 비교가 올바르게 동작한다.")
    fun t2() {
        assertThat(Calibration(1.0)).isEqualTo(Calibration(1.0))
        assertThat(Calibration(1.0)).isEqualTo(Calibration.IDENTITY)
        assertThat(Calibration(0.0)).isEqualTo(Calibration.VOID)
        assertThat(Calibration(1.0)).isNotEqualTo(Calibration(1.000000001))
        assertThat(Calibration(2.0)).isNotEqualTo(Calibration(2.000000001))
    }

    @Test
    @DisplayName("calibrate 메서드는 정밀도를 유지한다.")
    fun t3() {
        val calibration = Calibration(1.0 / 3.0)
        val result = calibration.calibrate(100)

        assertThat(result.toPlainString()).startsWith("33.33")
    }

    @Test
    @DisplayName("calibrate 메서드는 입력값에 계수를 곱한 결과를 반환한다.")
    fun t4() {
        assertThat(Calibration.VOID.calibrate(100)).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(Calibration.IDENTITY.calibrate(100)).isEqualByComparingTo(BigDecimal.valueOf(100))
        assertThat(Calibration(2.0).calibrate(100)).isEqualByComparingTo(BigDecimal.valueOf(200))
        assertThat(Calibration(1.25).calibrate(100)).isEqualByComparingTo(BigDecimal.valueOf(125))
    }
}
