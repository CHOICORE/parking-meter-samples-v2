package me.choicore.samples.operation.context

import com.fasterxml.jackson.databind.ObjectMapper

internal object ObjectMappers {
    val INSTANCE: ObjectMapper = ApplicationContextHolder.applicationContext.getBean(ObjectMapper::class.java)
}
