package me.choicore.samples.support.cache

import me.choicore.samples.core.TypeReference
import java.time.Duration

interface CacheClient {
    fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        clazz: Class<T>,
    ): T?

    fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        typeReference: TypeReference<T>,
    ): T?

    fun <T : Any> put(
        namespace: Namespace,
        key: CacheKey,
        value: T,
        duration: Duration,
    )

    fun evict(
        namespace: Namespace,
        key: CacheKey,
    ): Boolean

    fun evict(namespace: Namespace): Boolean
}
