package me.choicore.samples.support.cache

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringStrategy
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@TestConstructor(autowireMode = ALL)
class L2CacheClientTests(
    private val l2CacheClient: L2CacheClient,
) {
    @Test
    fun t1() {
        val meteringStrategy =
            DayOfWeekBasedMeteringStrategy(
                timelineMeter = TimelineMeter(),
                effectiveDate = LocalDate.now(),
            )

        val meteringRule =
            MeteringRule(
                id = PrimaryKey(1L),
                lotId = SecondaryKey(1L),
                meteringStrategy = meteringStrategy,
                registeredAt = LocalDateTime.now(),
                registeredBy = AuditorContext.identifier,
            )

        val namespace = Namespace("metering-rules")
        val key = CacheKey("${meteringRule.lotId}:${meteringRule.effectiveDate}")

        val get = l2CacheClient.get(namespace, key, MeteringStrategy::class.java)
        println(get)

        val evict = l2CacheClient.evict(namespace)
        println(evict)
    }
}
