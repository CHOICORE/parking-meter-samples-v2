package me.choicore.samples.context.entity

import java.time.LocalDateTime

interface Auditable {
    val registeredAt: LocalDateTime
    val registeredBy: String
    var modifiedAt: LocalDateTime?
    var modifiedBy: String?
    var deletedAt: LocalDateTime?
    var deletedBy: String?

    val deleted get() = deletedAt != null
}
