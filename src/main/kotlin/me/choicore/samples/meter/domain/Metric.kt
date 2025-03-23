package me.choicore.samples.meter.domain

import java.math.BigDecimal
import java.time.Duration

data class Metric(
    val measurand: Measurand,
    val measurer: Measurer,
    val usage: Duration,
) {
    // FIXME: 계산 로직이 복잡해지면 주입 받거나 계산을 처리할 별도의 클래스 분리 검토
    val cost: BigDecimal = measurer.calibration.calibrate(usage.toMinutes())
}
