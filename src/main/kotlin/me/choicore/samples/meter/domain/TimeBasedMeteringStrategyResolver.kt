package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.TimeBasedMeteringStrategy.AbstractTimeBasedMeteringStrategy
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class TimeBasedMeteringStrategyResolver(
    private val timeBasedMeteringStrategyRegistry: TimeBasedMeteringStrategyRegistry,
) {
    fun resolve(measurand: Measurand): TimeBasedMeteringStrategy {
        val timeBasedMeteringStrategy: TimeBasedMeteringStrategy? =
            this.timeBasedMeteringStrategyRegistry.getAvailableTimeBasedMeteringStrategy(
                lotId = ForeignKey(measurand.lotId),
                measureOn = measurand.measureOn,
            )

        return timeBasedMeteringStrategy ?: DEFAULT
    }

    companion object {
        val DEFAULT: TimeBasedMeteringStrategy =
            object : AbstractTimeBasedMeteringStrategy() {
                override val effectiveDate: LocalDate
                    get() = LocalDate.now()
                override val meteringMode: MeteringMode
                    get() = ONCE

                override fun applies(measuredOn: LocalDate): Boolean = true
            }
    }
}
