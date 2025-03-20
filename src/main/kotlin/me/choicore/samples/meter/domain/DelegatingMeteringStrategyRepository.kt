package me.choicore.samples.meter.domain

import me.choicore.samples.context.entity.ForeignKey
import me.choicore.samples.meter.domain.MeteringStrategy.AllDayMeteringStrategy
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
class DelegatingMeteringStrategyRepository(
    repositories: List<MeteringStrategyRepository<out AllDayMeteringStrategy>>,
) : MeteringStrategyRepository<AllDayMeteringStrategy> {
    override val supported: Class<AllDayMeteringStrategy> = AllDayMeteringStrategy::class.java

    override fun findByLotId(lotId: ForeignKey): List<AllDayMeteringStrategy> = this.delegates.values.flatMap { it.findByLotId(lotId) }

    private val delegates: Map<Class<out MeteringStrategy>, MeteringStrategyRepository<out AllDayMeteringStrategy>> =
        repositories.associateBy { it.supported }

    @Suppress("unchecked_cast")
    override fun save(meteringStrategy: AllDayMeteringStrategy): AllDayMeteringStrategy {
        val delegate: MeteringStrategyRepository<out AllDayMeteringStrategy> =
            delegates[meteringStrategy::class.java]
                ?: throw UnsupportedOperationException("Unsupported metering strategy: $meteringStrategy")

        return (delegate as MeteringStrategyRepository<AllDayMeteringStrategy>).save(meteringStrategy)
    }
}
