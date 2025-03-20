package me.choicore.samples.support.exposed

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

abstract class AuditableLongIdTable(
    name: String,
    columnName: String,
) : LongIdTable(name = name, columnName = columnName) {
    val registeredAt = datetime(name = "registered_at").default(LocalDateTime.now())
    val registeredBy = varchar(name = "registered_by", length = 255)
    val lastModifiedAt = datetime(name = "last_modified_at").nullable()
    val lastModifiedBy = varchar(name = "last_modified_by", length = 255).nullable()
    val deletedAt = datetime(name = "deleted_at").nullable()
    val deletedBy = varchar(name = "deleted_by", length = 255).nullable()
}
