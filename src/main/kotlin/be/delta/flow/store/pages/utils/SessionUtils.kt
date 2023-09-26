package be.delta.flow.store.pages.utils

import be.delta.flow.store.model.Store
import be.delta.flow.store.model.StoreName
import be.delta.flow.store.pages.utils.OpenedStores.Companion.storeNames
import io.ktor.server.application.*
import io.ktor.server.sessions.*


val ApplicationCall.openedStores: Set<StoreName>
    get() = sessions.get<OpenedStores>().storeNames

fun ApplicationCall.isStoreOpen(store: Store): Boolean = store.secret == null || storeName in openedStores

fun ApplicationCall.addOpenedStore(storeName: StoreName) {
    sessions.set(OpenedStores.fromStoreNames(openedStores + storeName))
}

data class OpenedStores(val values: Set<String>) {
    companion object {
        val OpenedStores?.storeNames: Set<StoreName>
            get() = this?.values.orEmpty().map { StoreName.parse(it).value }.toSet()

        fun fromStoreNames(storeNames: Set<StoreName>) = OpenedStores(storeNames.map { it.value }.toSet())
    }
}