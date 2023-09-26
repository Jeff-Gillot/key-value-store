package be.delta.flow.store.storage.tables

import org.jetbrains.exposed.sql.*

object EntryTable : Table("entries") {
    val store = varchar("store_name", 200).references(StoreTable.name)
    val key = varchar("key", 1024)
    val type = varchar("type", 200)
    val content = blob("blob")

    override val primaryKey: PrimaryKey = PrimaryKey(store, key)
}

