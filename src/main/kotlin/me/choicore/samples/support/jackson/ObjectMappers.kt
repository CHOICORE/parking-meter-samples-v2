package me.choicore.samples.support.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.context.ApplicationContextHolder

internal object ObjectMappers {
    val INSTANCE: ObjectMapper = ApplicationContextHolder.applicationContext.getBean(ObjectMapper::class.java)
}
