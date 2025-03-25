package me.choicore.samples.meter.infrastructure.ephemeral

import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.Measurand
import me.choicore.samples.meter.domain.MeteringMode
import me.choicore.samples.meter.domain.MeteringMode.ONCE
import me.choicore.samples.meter.domain.MeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.AbstractMeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategyProvider
import me.choicore.samples.meter.domain.MeteringStrategySelector
import me.choicore.samples.support.cache.CacheKey
import me.choicore.samples.support.cache.L1CacheClient
import me.choicore.samples.support.cache.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class CachedMeteringStrategySelector(
    private val meteringStrategyProvider: MeteringStrategyProvider,
    private val l1CacheClient: L1CacheClient,
) : MeteringStrategySelector {
    private val namespace: Namespace = Namespace("p-mr")
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd")

    private fun getCacheKey(measurand: Measurand): CacheKey =
        CacheKey("${measurand.lotId}:${measurand.measureOn.format(dateTimeFormatter)}")

    override fun select(measurand: Measurand): MeteringStrategy {
        if (measurand.measureOn == LocalDate.now()) {
            return getEnsuredMeteringStrategy(measurand)
        }

        val cacheKey: CacheKey = getCacheKey(measurand)

        val l1Cache: MeteringStrategy? = l1CacheClient.get(namespace, cacheKey, MeteringStrategy::class.java)

        if (l1Cache != null) {
            log.debug("L1 cache hit for key: {}", "$namespace:$cacheKey")
            return l1Cache
        }

        log.debug("L1 cache miss for key: {}", "$namespace:$cacheKey")

        return try {
            val found: MeteringStrategy = getEnsuredMeteringStrategy(measurand)
            l1CacheClient.put(namespace, cacheKey, found, Duration.ofMinutes(30))
            log.debug("Cache populated with data from provider for key: {}", "$namespace:$cacheKey")
            found
        } catch (e: Exception) {
            log.error("Error fetching metering strategy, using DEFAULT: {}", e.message, e)
            DEFAULT
        }
    }

    private fun getEnsuredMeteringStrategy(measurand: Measurand): MeteringStrategy {
        val found: MeteringStrategy =
            meteringStrategyProvider.getAvailableTimeBasedMeteringStrategy(
                SecondaryKey(measurand.lotId),
                measurand.measureOn,
            ) ?: DEFAULT
        return found
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
        private val log: Logger = LoggerFactory.getLogger(CachedMeteringStrategySelector::class.java)
    }
}
