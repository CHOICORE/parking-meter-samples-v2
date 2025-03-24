package me.choicore.samples.support.cache

@JvmInline
value class CacheKey(
    val value: String,
) {
    fun withNamespace(namespace: Namespace): String = "${namespace.value}:$value"

    companion object {
        fun of(
            namespace: Namespace,
            key: String,
        ): CacheKey = CacheKey(value = "$namespace:$key")
    }
}
