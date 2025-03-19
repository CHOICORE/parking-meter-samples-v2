package me.choicore.samples.operation.context.entity

@JvmInline
value class ForeignKey(
    val value: Long,
) {
    init {
        require(value > 0) { "Foreign key requires a strictly positive value" }
    }
}
