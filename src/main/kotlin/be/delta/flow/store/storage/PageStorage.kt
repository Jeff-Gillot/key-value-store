package be.delta.flow.store.storage

import be.delta.flow.store.model.Entry
import be.delta.flow.store.model.Key
import be.delta.flow.store.model.Store
import be.delta.flow.store.model.StoreName
import be.delta.flow.store.storage.tables.EntryRepository
import be.delta.flow.store.storage.tables.StoreRepository
import org.jetbrains.exposed.sql.transactions.transaction

class PageStorage(
    private val storeRepository: StoreRepository,
    private val entryRepository: EntryRepository,
) {

    fun getStoreOrNull(storeName: StoreName): Store? {
        return transaction {
            storeRepository.getOrNull(storeName)
        }
    }

    fun getStore(storeName: StoreName): Store {
        return transaction {
            storeRepository.get(storeName)
        }
    }

    fun getStoreEntries(storeName: StoreName): List<Entry> {
        return transaction {
            entryRepository.list(storeName)
        }
    }

    fun listStores(): List<Store> {
        return transaction {
            storeRepository.listStores()
        }
    }

    fun getEntry(storeName: StoreName, key: Key): Entry {
        return transaction {
            entryRepository.get(storeName, key)
        }
    }

    fun updateEntry(entry: Entry) {
        transaction {
            entryRepository.insertOrUpdate(entry.storeName, entry.key, entry.type.value, entry.content)
        }
    }

    fun createNewStore(store: Store) {
        transaction {
            storeRepository.insert(store)
        }
    }

    fun deleteStore(storeName: StoreName) {
        transaction {
            entryRepository.deleteForStore(storeName)
            storeRepository.delete(storeName)
        }
    }

    fun createEntry(newEntry: Entry) {
        transaction {
            entryRepository.insert(newEntry)
        }
    }

    fun getEntryOrNull(storeName: StoreName, key: Key): Entry? {
        return transaction {
            entryRepository.getOrNull(storeName, key)
        }
    }

    fun deleteEntry(storeName: StoreName, key: Key) {
        transaction {
            entryRepository.delete(storeName, key)
        }
    }
}