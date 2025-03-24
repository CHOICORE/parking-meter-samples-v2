package me.choicore.samples.support.cache

import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.core.TypeReference
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Service
import java.time.Duration

// L2 캐시 클라이언트 (Redis 기반) - 인터페이스 구현
@Service
class L2CacheClient(
    redisConnectionFactory: RedisConnectionFactory,
    objectMapper: ObjectMapper,
) : CacheClient {
    private val redisTemplate = RedisTemplate<String, Any>()
    private val objectMapper: ObjectMapper = objectMapper.copy().apply { deactivateDefaultTyping() }

    init {
        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer(this.objectMapper)
        this.redisTemplate.connectionFactory = redisConnectionFactory
        this.redisTemplate.keySerializer = StringRedisSerializer()
        this.redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        this.redisTemplate.hashKeySerializer = StringRedisSerializer()
        this.redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer
        this.redisTemplate.afterPropertiesSet()
    }

    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        clazz: Class<T>,
    ): T? {
        val value: Any = redisTemplate.opsForValue().get(formatKey(namespace, key)) ?: return null
        return objectMapper.convertValue(value, clazz)
    }

    override fun <T : Any> get(
        namespace: Namespace,
        key: CacheKey,
        typeReference: TypeReference<T>,
    ): T? {
        val value: Any = redisTemplate.opsForValue().get(formatKey(namespace, key)) ?: return null
        return objectMapper.convertValue(value, objectMapper.typeFactory.constructType(typeReference.type))
    }

    override fun <T : Any> put(
        namespace: Namespace,
        key: CacheKey,
        value: T,
        duration: Duration,
    ) {
        redisTemplate.opsForValue().set(formatKey(namespace, key), value, duration)
    }

    override fun evict(
        namespace: Namespace,
        key: CacheKey,
    ): Boolean = redisTemplate.delete(formatKey(namespace, key))

    override fun evict(namespace: Namespace): Boolean {
        val pattern = "${namespace.value}:*"
        val keys = redisTemplate.keys(pattern)
        if (keys.isNotEmpty()) {
            return redisTemplate.delete(keys) > 0
        }
        return false
    }

    private fun formatKey(
        namespace: Namespace,
        key: CacheKey,
    ): String = "${namespace.value}:${key.value}"
}
