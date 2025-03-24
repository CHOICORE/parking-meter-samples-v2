package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.SecondaryKey
import java.time.LocalDate

interface MeteringStrategyProvider {
    fun getAvailableTimeBasedMeteringStrategy(
        lotId: SecondaryKey,
        measureOn: LocalDate,
    ): MeteringStrategy?
}
