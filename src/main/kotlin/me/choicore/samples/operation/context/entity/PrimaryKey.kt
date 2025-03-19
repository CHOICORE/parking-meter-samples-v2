package me.choicore.samples.operation.context.entity

@JvmInline
value class PrimaryKey(
    val value: Long,
) {
    companion object {
        val UNASSIGNED = PrimaryKey(value = 0)
    }
}
