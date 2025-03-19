package me.choicore.samples.operation.context.entity

private const val DEFAULT_AUDITOR_IDENTIFIER = "system"

object AuditorContext {
    private val context: ThreadLocal<String?> = ThreadLocal<String?>()

    fun set(username: String) {
        context.set(username)
    }

    fun clear() {
        context.remove()
    }

    val identifier: String
        get() = context.get() ?: DEFAULT_AUDITOR_IDENTIFIER

    private inline fun <T> runAs(
        username: String,
        block: () -> T,
    ): T {
        val previous: String = this.identifier
        try {
            set(username)
            return block()
        } finally {
            if (previous == DEFAULT_AUDITOR_IDENTIFIER) {
                clear()
            } else {
                set(previous)
            }
        }
    }

    operator fun <T> invoke(
        username: String = DEFAULT_AUDITOR_IDENTIFIER,
        block: () -> T,
    ): T = this.runAs(username, block)
}
