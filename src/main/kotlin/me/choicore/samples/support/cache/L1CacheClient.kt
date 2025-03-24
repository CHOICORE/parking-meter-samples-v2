package me.choicore.samples.support.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import me.choicore.samples.core.TypeReference
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class L1CacheClient : CacheClient {
    private val caches: ConcurrentHashMap<Namespace, Cache<CacheKey, Any>> = ConcurrentHashMap()

    private fun register(
        namespace: Namespace,
        duration: Duration,
    ): Cache<CacheKey, Any> =
        caches.computeIfAbsent(namespace) {
            Caffeine
                .newBuilder()
                .expireAfterWrite(duration)
                .maximumSize(10000)
                .build()
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        clazz: Class<T>,
    ): T? {
        val cache = caches[namespace] ?: return null
        val value = cache.getIfPresent(key) ?: return null

        return if (clazz.isInstance(value)) {
            value as T
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        typeReference: TypeReference<T>,
    ): T? {
        val cache = caches[namespace] ?: return null
        val value = cache.getIfPresent(key) ?: return null

        return try {
            value as T
        } catch (e: ClassCastException) {
            null
        }
    }

    override fun <T : Any> put(
        namespace: Namespace,
        key: CacheKey,
        value: T,
        duration: Duration,
    ) {
        val cache = register(namespace, duration)
        cache.put(key, value)
    }

    override fun evict(
        namespace: Namespace,
        key: CacheKey,
    ): Boolean {
        val cache = caches[namespace] ?: return false
        cache.invalidate(key)
        return true
    }

    override fun evict(namespace: Namespace): Boolean {
        val cache = caches[namespace] ?: return false
        cache.invalidateAll()
        return true
    }
}
