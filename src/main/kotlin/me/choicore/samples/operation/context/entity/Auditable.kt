package me.choicore.samples.operation.context.entity

import java.time.LocalDateTime

interface Auditable {
    val registeredAt: LocalDateTime
    val registeredBy: String
    val modifiedAt: LocalDateTime?
    val modifiedBy: String?
    val deletedAt: LocalDateTime?
    val deletedBy: String?

    val deleted get() = deletedAt != null
}
