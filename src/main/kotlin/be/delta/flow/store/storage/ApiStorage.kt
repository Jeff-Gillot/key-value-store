package be.delta.flow.store.storage

import be.delta.flow.store.model.*
import be.delta.flow.store.routes.UnauthorizedException
import be.delta.flow.store.storage.tables.StoreRepository
import be.delta.flow.store.storage.tables.EntryRepository
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ApiStorage(
    private val storeRepository: StoreRepository,
    private val entryRepository: EntryRepository,
) {
    suspend fun insertStore(storeName: StoreName, storeSecret: StoreSecret?) = backgroundTransaction {
        val store = getOrCreateStore(storeName, storeSecret)
        validateStoreKey(store, storeSecret)
    }

    suspend fun listStoreKeys(storeName: StoreName, storeSecret: StoreSecret?): List<Key> = backgroundTransaction {
        validateStoreKey(storeName, storeSecret)
        entryRepository.listKeysForStore(storeName)
    }

    suspend fun deleteStore(storeName: StoreName, storeSecret: StoreSecret?) = backgroundTransaction {
        validateStoreKey(storeName, storeSecret)
        entryRepository.deleteForStore(storeName)
        storeRepository.delete(storeName)
    }

    suspend fun getEntryOrNull(storeName: StoreName, storeSecret: StoreSecret?, key: Key) = backgroundTransaction {
        validateStoreKey(storeName, storeSecret)
        entryRepository.getOrNull(storeName, key)
    }

    suspend fun insertOrUpdateEntry(storeName: StoreName, storeSecret: StoreSecret?, key: Key, contentType: ContentType, content: Content) = backgroundTransaction {
        validateStoreKey(storeName, storeSecret)
        val previousEntry = entryRepository.getOrNull(storeName, key)
        entryRepository.insertOrUpdate(storeName, key, contentType, content)
        previousEntry
    }

    suspend fun deleteEntry(storeName: StoreName, storeSecret: StoreSecret?, key: Key) = backgroundTransaction {
        validateStoreKey(storeName, storeSecret)
        val previousEntry = entryRepository.getOrNull(storeName, key)
        entryRepository.delete(storeName, key)
        previousEntry
    }

    private fun getOrCreateStore(storeName: StoreName, storeSecret: StoreSecret?): Store {
        var store = storeRepository.getOrNull(storeName)

        if (store == null) {
            store = Store(storeName, storeSecret)
            storeRepository.insert(store)
        }

        return store
    }

    private fun validateStoreKey(storeName: StoreName, storeSecret: StoreSecret?) {
        val store = getOrCreateStore(storeName, storeSecret)
        if (store.secret != storeSecret) {
            throw UnauthorizedException("Invalid store key")
        }
    }

    private fun validateStoreKey(store: Store, storeSecret: StoreSecret?) {
        if (store.secret != storeSecret) {
            throw UnauthorizedException("Invalid store key")
        }
    }

    private suspend fun <T> backgroundTransaction(block: suspend Transaction.() -> T): T {
        return newSuspendedTransaction(context = currentCoroutineContext() + Dispatchers.IO, statement = block)
    }
}