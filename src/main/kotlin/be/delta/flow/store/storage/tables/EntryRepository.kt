package be.delta.flow.store.storage.tables

import be.delta.flow.store.model.Content
import be.delta.flow.store.model.Content.Companion.toContent
import be.delta.flow.store.model.Entry
import be.delta.flow.store.model.Key
import be.delta.flow.store.model.Key.Companion.toKey
import be.delta.flow.store.model.StoreName
import be.delta.flow.store.model.StoreName.Companion.toStoreName
import be.delta.flow.store.model.Type.Companion.toType
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

@Suppress("RemoveRedundantQualifierName")
object EntryRepository {
    fun listKeysForStore(storeName: StoreName): List<Key> {
        return EntryTable.slice(EntryTable.key).select { EntryTable.store eq storeName.value }.map { it[EntryTable.key].toKey() }
    }

    fun deleteForStore(storeName: StoreName) {
        EntryTable.deleteWhere { EntryTable.store eq storeName.value }
    }

    fun getOrNull(storeName: StoreName, key: Key): Entry? {
        return EntryTable.select { (EntryTable.store eq storeName.value) and (EntryTable.key eq key.value) }.singleOrNull()?.toEntry()
    }

    fun insert(entry: Entry) {
        EntryTable.insert {
            it[EntryTable.store] = entry.storeName.value
            it[EntryTable.key] = entry.key.value
            it[EntryTable.type] = entry.type.toString()
            it[EntryTable.content] = ExposedBlob(entry.content.byteArray)
        }
    }

    fun insertOrUpdate(storeName: StoreName, key: Key, contentType: ContentType, content: Content) {
        EntryTable.replace {
            it[EntryTable.store] = storeName.value
            it[EntryTable.key] = key.value
            it[EntryTable.type] = contentType.toString()
            it[EntryTable.content] = ExposedBlob(content.byteArray)
        }
    }

    fun delete(storeName: StoreName, key: Key) {
        EntryTable.deleteWhere { (EntryTable.store eq storeName.value) and (EntryTable.key eq key.value) }
    }


    fun list(storeName: StoreName): List<Entry> {
        return EntryTable.select { EntryTable.store eq storeName.value }.map { it.toEntry() }
    }

    private fun ResultRow.toEntry(): Entry {
        return Entry(
            key = this[EntryTable.key].toKey(),
            type = this[EntryTable.type].toType(),
            storeName = this[EntryTable.store].toStoreName(),
            content = this[EntryTable.content].bytes.toContent(),
        )
    }

    fun get(storeName: StoreName, key: Key): Entry = getOrNull(storeName, key)!!

}