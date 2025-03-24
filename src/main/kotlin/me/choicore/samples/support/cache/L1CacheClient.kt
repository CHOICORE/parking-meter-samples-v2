package me.choicore.samples.support.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import me.choicore.samples.core.TypeReference
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class L1CacheClient : CacheClient {
    private val caches: ConcurrentHashMap<Namespace, Cache<CacheKey, ExpiryAware<Any>>> = ConcurrentHashMap()

    private data class ExpiryAware<V : Any>(
        val value: V,
        val timeout: Duration,
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        clazz: Class<T>,
    ): T? {
        val cache: Cache<CacheKey, ExpiryAware<Any>> = caches[namespace] ?: return null
        val entry: ExpiryAware<Any> = cache.getIfPresent(key) ?: return null
        return entry.value as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        typeReference: TypeReference<T>,
    ): T? {
        val cache: Cache<CacheKey, ExpiryAware<Any>> = caches[namespace] ?: return null
        val entry: ExpiryAware<Any> = cache.getIfPresent(key) ?: return null

        return entry.value as T
    }

    override fun <T : Any> put(
        namespace: Namespace,
        key: CacheKey,
        value: T,
        duration: Duration,
    ) {
        val cache: Cache<CacheKey, ExpiryAware<Any>> = register(namespace)
        cache.put(key, ExpiryAware(value, duration))
    }

    override fun evict(
        namespace: Namespace,
        key: CacheKey,
    ): Boolean {
        val cache: Cache<CacheKey, ExpiryAware<Any>> = caches[namespace] ?: return false

        return try {
            cache.invalidate(key)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun evict(namespace: Namespace): Boolean {
        val cache: Cache<CacheKey, ExpiryAware<Any>> = caches[namespace] ?: return false
        return try {
            cache.invalidateAll()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun register(namespace: Namespace): Cache<CacheKey, ExpiryAware<Any>> =
        caches.computeIfAbsent(namespace) {
            Caffeine
                .newBuilder()
                .expireAfter(
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
                ).maximumSize(10000)
                .build()
        }
}
