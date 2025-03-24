package me.choicore.samples.support.cache

import me.choicore.samples.context.entity.AuditorContext
import me.choicore.samples.context.entity.PrimaryKey
import me.choicore.samples.context.entity.SecondaryKey
import me.choicore.samples.meter.domain.MeteringRule
import me.choicore.samples.meter.domain.MeteringStrategy.DayOfWeekBasedMeteringStrategy
import me.choicore.samples.meter.domain.TimelineMeter
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class L1CacheClientTests(
    private val l1CacheClient: L1CacheClient,
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

        l1CacheClient.put(namespace, key, meteringRule, Duration.ofHours(1))
        val get = l1CacheClient.get(namespace, key, MeteringRule::class.java)
        println(get)
    }
}
