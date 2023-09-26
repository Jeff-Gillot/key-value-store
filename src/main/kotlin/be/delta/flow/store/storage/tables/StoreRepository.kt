package be.delta.flow.store.storage.tables

import be.delta.flow.store.model.Store
import be.delta.flow.store.model.StoreName
import be.delta.flow.store.model.StoreName.Companion.toStoreName
import be.delta.flow.store.model.StoreSecret.Companion.toStoreSecret
import be.delta.flow.store.storage.tables.StoreTable.name
import be.delta.flow.store.storage.tables.StoreTable.secret
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object StoreRepository {
    fun get(storeName: StoreName): Store =
        getOrNull(storeName)!!

    fun getOrNull(storeName: StoreName): Store? =
        StoreTable.select { name eq storeName.value }.singleOrNull()?.toStore()

    fun insert(store: Store) {
        StoreTable.insert {
            it[name] = store.name.value
            it[secret] = store.secret?.value
        }
    }

    fun delete(storeName: StoreName) {
        StoreTable.deleteWhere { name eq storeName.value }
    }

    fun listStores(): List<Store> {
        return StoreTable.selectAll().map { it.toStore() }
    }

    private fun ResultRow.toStore(): Store {
        return Store(
            name = this[name].toStoreName(),
            secret = this[secret]?.toStoreSecret()
        )
    }
}