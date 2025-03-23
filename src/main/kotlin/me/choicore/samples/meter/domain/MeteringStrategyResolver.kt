package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringStrategy.AbstractMeteringStrategy
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MeteringStrategyResolver(
    private val meteringStrategyRegistry: MeteringStrategyRegistry,
) {
    fun resolve(measurand: Measurand): MeteringStrategy {
        val meteringStrategy: MeteringStrategy? =
            this.meteringStrategyRegistry.getAvailableTimeBasedMeteringStrategy(
                lotId = ForeignKey(measurand.lotId),
                measureOn = measurand.measureOn,
            )

        return meteringStrategy ?: DEFAULT
    }

    companion object {
        val DEFAULT: MeteringStrategy =
            object : AbstractMeteringStrategy() {
                override val effectiveDate: LocalDate
                    get() = LocalDate.now()
                override val meteringMode: MeteringMode
                    get() = ONCE

                override fun applies(measuredOn: LocalDate): Boolean = true
            }
    }
}
