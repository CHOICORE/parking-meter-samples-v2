package me.choicore.samples.context.entity

@JvmInline
value class SecondaryKey(
    val value: Long,
) {
    init {
        require(value > 0) { "Foreign key requires a strictly positive value" }
    }

    override fun toString(): String = value.toString()
}
