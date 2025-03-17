package me.choicore.samples.operation.context

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
internal class ApplicationContextHolder(
    applicationContext: ApplicationContext,
) {
    companion object {
        @JvmStatic
        lateinit var applicationContext: ApplicationContext
            private set
    }

    init {
        Companion.applicationContext = applicationContext
    }
}
