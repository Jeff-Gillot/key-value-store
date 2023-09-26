package be.delta.flow.store.storage.tables

import org.jetbrains.exposed.sql.Table

object StoreTable : Table("stores") {
    val name = varchar("name", 200).uniqueIndex()
    val secret = varchar("secret", 1024).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(name)
}