package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import java.time.LocalDate

interface MeteringStrategyProvider {
    fun getAvailableTimeBasedMeteringStrategy(
        lotId: ForeignKey,
        measureOn: LocalDate,
    ): MeteringStrategy?
}
