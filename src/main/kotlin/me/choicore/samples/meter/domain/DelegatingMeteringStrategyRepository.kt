package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringStrategy.TimeSlotMeteringStrategy
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
class DelegatingMeteringStrategyRepository(
    repositories: List<MeteringStrategyRepository<out TimeSlotMeteringStrategy>>,
) : MeteringStrategyRepository<TimeSlotMeteringStrategy> {
    override val supported: Class<TimeSlotMeteringStrategy> = TimeSlotMeteringStrategy::class.java

    override fun findByLotId(lotId: ForeignKey): List<TimeSlotMeteringStrategy> = this.delegates.values.flatMap { it.findByLotId(lotId) }

    private val delegates: Map<Class<out MeteringStrategy>, MeteringStrategyRepository<out TimeSlotMeteringStrategy>> =
        repositories.associateBy { it.supported }

    @Suppress("UNCHECKED_CAST")
    override fun save(meteringStrategy: TimeSlotMeteringStrategy): TimeSlotMeteringStrategy {
        val delegate: MeteringStrategyRepository<out TimeSlotMeteringStrategy> =
            delegates[meteringStrategy::class.java]
                ?: throw UnsupportedOperationException("Unsupported metering strategy: $meteringStrategy")

        return (delegate as MeteringStrategyRepository<TimeSlotMeteringStrategy>).save(meteringStrategy)
    }
}
