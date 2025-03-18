package me.choicore.samples.operation.context

data object AuditorContext {
    private val holder: ThreadLocal<String> = ThreadLocal<String>()

    fun set(value: String) {
        this.holder.set(value)
    }

    val current: String get() = this.holder.get()

    val clear: () -> Unit = {
        holder.remove()
    }
}
