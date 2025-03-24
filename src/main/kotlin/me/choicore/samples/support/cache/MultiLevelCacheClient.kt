// package me.choicore.samples.support.cache
//
// import me.choicore.samples.core.TypeReference
// import org.springframework.stereotype.Service
// import java.time.Duration
//
// @Service
// class MultiLevelCacheClient(
//    private val l1CacheClient: L1CacheClient,
//    private val l2CacheClient: L2CacheClient,
// ) : CacheClient {
//    override fun <T : Any> get(
//        namespace: Namespace,
//        key: CacheKey,
//        clazz: Class<T>,
//    ): T? =
//        getFromHierarchy(
//            namespace = namespace,
//            key = key,
//            fetch = { client -> client.get(namespace, key, clazz) },
//            saveToL1 = { value -> l1CacheClient.put(namespace, key, value) },
//        )
//
//    override fun <T : Any> get(
//        namespace: Namespace,
//        key: CacheKey,
//        typeReference: TypeReference<T>,
//    ): T? =
//        getFromHierarchy(
//            namespace = namespace,
//            key = key,
//            fetch = { client -> client.get(namespace, key, typeReference) },
//            saveToL1 = { value -> l1CacheClient.put(namespace, key, value) },
//        )
//
//    private fun <T : Any> getFromHierarchy(
//        namespace: Namespace,
//        key: CacheKey,
//        fetch: (CacheClient) -> T?,
//        saveToL1: (T) -> Unit,
//    ): T? {
//        val l1 = fetch(l1CacheClient)
//        if (l1 != null) return l1
//
//        val l2 = fetch(l2CacheClient)
//        if (l2 != null) saveToL1(l2)
//        return l2
//    }
//
//    override fun <T : Any> put(
//        namespace: Namespace,
//        key: CacheKey,
//        value: T,
//        duration: Duration,
//    ) {
//        l1CacheClient.put(namespace, key, value, duration)
//        l2CacheClient.put(namespace, key, value, duration)
//    }
//
//    override fun evict(
//        namespace: Namespace,
//        key: CacheKey,
//    ): Boolean = l1CacheClient.evict(namespace, key) || l2CacheClient.evict(namespace, key)
//
//    override fun evict(namespace: Namespace): Boolean = l1CacheClient.evict(namespace) || l2CacheClient.evict(namespace)
//
//    fun <T : Any> get(
//        namespace: Namespace,
//        key: CacheKey,
//        clazz: Class<T>,
//        level: CacheLevel,
//    ): T? = level.select().get(namespace, key, clazz)
//
//    fun <T : Any> get(
//        namespace: Namespace,
//        key: CacheKey,
//        typeReference: TypeReference<T>,
//        level: CacheLevel,
//    ): T? = level.select().get(namespace, key, typeReference)
//
//    fun <T : Any> put(
//        namespace: Namespace,
//        key: CacheKey,
//        value: T,
//        level: CacheLevel,
//        timeout: Duration,
//    ) = level.select().put(namespace, key, value, timeout)
//
//    fun evict(
//        namespace: Namespace,
//        key: CacheKey,
//        level: CacheLevel,
//    ): Boolean = level.select().evict(namespace, key)
//
//    fun evict(
//        namespace: Namespace,
//        level: CacheLevel,
//    ): Boolean = level.select().evict(namespace)
//
//    fun <T : Any> aside(
//        namespace: Namespace,
//        key: CacheKey,
//        clazz: Class<T>,
//        level: CacheLevel,
//        timeout: Duration,
//        loader: () -> T,
//    ): T =
//        asideTemplate(
//            namespace = namespace,
//            key = key,
//            get = { get(namespace, key, clazz, level) },
//            put = { value -> put(namespace, key, value, level, timeout) },
//            loader = loader,
//        )
//
//    fun <T : Any> aside(
//        namespace: Namespace,
//        key: CacheKey,
//        clazz: Class<T>,
//        levels: Set<CacheLevel> = setOf(CacheLevel.L1, CacheLevel.L2),
//        timeout: Duration,
//        loader: () -> T,
//    ): T =
//        asideTemplate(
//            namespace = namespace,
//            key = key,
//            get = {
//                levels.firstNotNullOfOrNull { level -> level.select().get(namespace, key, clazz) }
//            },
//            put = { value ->
//                levels.forEach { level -> level.select().put(namespace, key, value, timeout) }
//            },
//            loader = loader,
//        )
//
//    fun <T : Any> aside(
//        namespace: Namespace,
//        key: CacheKey,
//        typeReference: TypeReference<T>,
//        levels: Set<CacheLevel> = setOf(CacheLevel.L1, CacheLevel.L2),
//        timeout: Duration,
//        loader: () -> T,
//    ): T =
//        asideTemplate(
//            namespace = namespace,
//            key = key,
//            get = {
//                levels.firstNotNullOfOrNull { level -> level.select().get(namespace, key, typeReference) }
//            },
//            put = { value ->
//                levels.forEach { level -> level.select().put(namespace, key, value, timeout) }
//            },
//            loader = loader,
//        )
//
//    fun <T : Any> aside(
//        namespace: Namespace,
//        key: CacheKey,
//        typeReference: TypeReference<T>,
//        level: CacheLevel,
//        timeout: Duration,
//        loader: () -> T,
//    ): T =
//        asideTemplate(
//            namespace = namespace,
//            key = key,
//            get = { get(namespace, key, typeReference, level) },
//            put = { value -> put(namespace, key, value, level, timeout) },
//            loader = loader,
//        )
//
//    private fun CacheLevel.select(): CacheClient =
//        when (this) {
//            CacheLevel.L1 -> l1CacheClient
//            CacheLevel.L2 -> l2CacheClient
//        }
//
//    private fun <T : Any> asideTemplate(
//        namespace: Namespace,
//        key: CacheKey,
//        get: () -> T?,
//        put: (T) -> Unit,
//        loader: () -> T,
//    ): T {
//        val cached = get()
//        if (cached != null) return cached
//
//        val value = loader()
//        put(value)
//        return value
//    }
// }
