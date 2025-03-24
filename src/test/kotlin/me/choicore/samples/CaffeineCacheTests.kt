package me.choicore.samples

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.Scheduler
import me.choicore.samples.support.cache.CacheKey
import org.junit.jupiter.api.Test
import java.time.Duration

class CaffeineCacheTests {
    @Test
    fun t1() {
        val build =
            Caffeine
                .newBuilder()
                .removalListener { key: CacheKey, value: ExpiryAware<Any>, cause: RemovalCause ->
                    println("Cache entry removed: key=$key, cause=$cause")
                }.expireAfter(
                    object : Expiry<CacheKey, ExpiryAware<Any>> {
                        override fun expireAfterCreate(
                            key: CacheKey,
                            value: ExpiryAware<Any>,
                            currentTime: Long,
                        ): Long = value.timeout.toNanos()

                        override fun expireAfterUpdate(
                            key: CacheKey,
                            value: ExpiryAware<Any>,
                            currentTime: Long,
                            currentDuration: Long,
                        ): Long = currentDuration

                        override fun expireAfterRead(
                            key: CacheKey,
                            value: ExpiryAware<Any>,
                            currentTime: Long,
                            currentDuration: Long,
                        ): Long = currentDuration
                    },
                ).scheduler(Scheduler.systemScheduler())
                .build<CacheKey, ExpiryAware<Any>>()

        build.put(CacheKey("k1"), ExpiryAware("v1", Duration.ofSeconds(1)))
        build.put(CacheKey("k2"), ExpiryAware("v2", Duration.ofSeconds(5)))

        val ifPresent = build.getIfPresent(CacheKey("k1"))
        println(ifPresent)

        for (i in 1..10) {
            Thread.sleep(1000)
            val ifPresent = build.getIfPresent(CacheKey("k1"))
        }
    }

    data class ExpiryAware<V>(
        val value: V,
        val timeout: Duration,
    )
}
