package me.choicore.samples.support.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
internal object ObjectMappers : ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    fun getInstance(): ObjectMapper = applicationContext.getBean(ObjectMapper::class.java)

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}
