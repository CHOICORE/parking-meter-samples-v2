package me.choicore.samples.support.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ObjectMappers(
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private lateinit var instance: ObjectMapper

        fun getInstance(): ObjectMapper = instance
    }

    init {
        instance = this.objectMapper
    }
}
