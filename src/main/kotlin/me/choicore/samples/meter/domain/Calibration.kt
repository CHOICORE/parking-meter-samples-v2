package me.choicore.samples.meter.domain

import java.math.BigDecimal

@JvmInline
value class Calibration(
    val factor: BigDecimal,
) {
    init {
        require(BigDecimal.ZERO <= factor && factor <= BigDecimal.TEN) {
            "factor must be between 0.0 and 10.0 (inclusive)"
        }
    }

    constructor(factor: Double) : this(BigDecimal.valueOf(factor))

    companion object {
        val IDENTITY = Calibration(BigDecimal.ONE.setScale(1))
        val VOID = Calibration(BigDecimal.ZERO.setScale(1))
    }

    fun calibrate(amount: Long): BigDecimal = calibrate(BigDecimal.valueOf(amount))

    fun calibrate(amount: BigDecimal): BigDecimal {
        if (this == VOID) {
            return BigDecimal.ZERO
        }
        return amount.multiply(this.factor)
    }
}
