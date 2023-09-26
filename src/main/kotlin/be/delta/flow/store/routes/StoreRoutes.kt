package be.delta.flow.store.routes

import be.delta.flow.store.model.*
import be.delta.flow.store.model.Content.Companion.toContent
import be.delta.flow.store.model.Key.Companion.toKey
import be.delta.flow.store.model.StoreName.Companion.toStoreName
import be.delta.flow.store.model.StoreSecret.Companion.toStoreSecret
import be.delta.flow.store.storage.ApiStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class StoreRoutes(
    val storage: ApiStorage
) {
    suspend fun postStore(call: ApplicationCall) {
        storage.insertStore(call.storeName, call.storeSecret)
        call.respond(HttpStatusCode.Accepted)
    }

    suspend fun putStore(call: ApplicationCall) {
        storage.insertStore(call.storeName, call.storeSecret)
        call.respond(HttpStatusCode.Accepted)
    }

    suspend fun getStore(call: ApplicationCall) {
        val keys = storage.listStoreKeys(call.storeName, call.storeSecret)
        call.respond(HttpStatusCode.OK, keys.map { it.value })
    }

    suspend fun deleteStore(call: ApplicationCall) {
        storage.deleteStore(call.storeName, call.storeSecret)
        call.respond(HttpStatusCode.Accepted)
    }

    suspend fun getEntry(call: ApplicationCall) {
        val entry = storage.getEntryOrNull(call.storeName, call.storeSecret, call.key)
        call.respondEntry(entry)
    }

    suspend fun postEntry(call: ApplicationCall) {
        val previousEntry = storage.insertOrUpdateEntry(call.storeName, call.storeSecret, call.key, call.contentType, call.receiveBytes())
        call.respondEntry(previousEntry)
    }

    suspend fun putEntry(call: ApplicationCall) {
        val previousEntry = storage.insertOrUpdateEntry(call.storeName, call.storeSecret, call.key, call.contentType, call.receiveBytes())
        call.respondEntry(previousEntry)
    }

    suspend fun deleteEntry(call: ApplicationCall) {
        val previousEntry = storage.deleteEntry(call.storeName, call.storeSecret, call.key)
        call.respondEntry(previousEntry)
    }

    private suspend fun ApplicationCall.respondEntry(entry: Entry?) = when (entry) {
        null -> respond(HttpStatusCode.NoContent)
        else -> respondBytes(entry.content.byteArray, entry.type.value, HttpStatusCode.Accepted)
    }

    val ApplicationCall.storeName: StoreName get() = parameters["store"]?.toStoreName()!!
    val ApplicationCall.storeSecret: StoreSecret? get() = request.headers["Authorization"]?.toStoreSecret()
    val ApplicationCall.key: Key get() = parameters.getAll("key")?.joinToString("/")?.toKey()!!
    val ApplicationCall.contentType: ContentType get() = request.contentType()
    suspend fun ApplicationCall.receiveBytes(): Content = receive<ByteArray>().toContent()
}