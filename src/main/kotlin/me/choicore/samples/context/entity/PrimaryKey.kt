package me.choicore.samples.context.entity

@JvmInline
value class PrimaryKey(
    val value: Long,
) {
    companion object {
        val UNINITIALIZED = PrimaryKey(value = 0)
    }

    override fun toString(): String = value.toString()
}
