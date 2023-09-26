package be.delta.flow.store

import be.delta.flow.store.pages.PageRoutes
import be.delta.flow.store.pages.defaultErrorPage
import be.delta.flow.store.pages.unauthorizedPage
import be.delta.flow.store.pages.utils.OpenedStores
import be.delta.flow.store.pages.utils.SessionStorageMemoryWithEviction
import be.delta.flow.store.parsing.ValidationException
import be.delta.flow.store.routes.StoreRoutes
import be.delta.flow.store.routes.UnauthorizedException
import be.delta.flow.store.storage.ApiStorage
import be.delta.flow.store.storage.PageStorage
import be.delta.flow.store.storage.tables.EntryRepository
import be.delta.flow.store.storage.tables.EntryTable
import be.delta.flow.store.storage.tables.StoreRepository
import be.delta.flow.store.storage.tables.StoreTable
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.minutes

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        while (true) {
            delay(300_000)
            System.gc()
        }
    }

    embeddedServer(Netty, port = 5000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureDatabase()
    configureJson()
    configureSession()
    configureStatusPages()

    val storeRepository = StoreRepository
    val entryRepository = EntryRepository
    val apiStorage = ApiStorage(storeRepository, entryRepository)
    val pageStorage = PageStorage(storeRepository, entryRepository)

    val storeRoutes = StoreRoutes(apiStorage)
    val pageRoutes = PageRoutes(pageStorage)

    routing {
        // Api
        get("/api/store/{store}") { storeRoutes.getStore(call) }
        post("/api/store/{store}") { storeRoutes.postStore(call) }
        put("/api/store/{store}") { storeRoutes.putStore(call) }
        delete("/api/store/{store}") { storeRoutes.deleteStore(call) }

        get("/api/store/{store}/{key...}") { storeRoutes.getEntry(call) }
        post("/api/store/{store}/{key...}") { storeRoutes.postEntry(call) }
        put("/api/store/{store}/{key...}") { storeRoutes.putEntry(call) }
        delete("/api/store/{store}/{key...}") { storeRoutes.deleteEntry(call) }

        // Pages
        get("/") { pageRoutes.home(call) }
        get("/store/{storeName}") { pageRoutes.viewStore(call) }
        post("/store/{storeName}") { pageRoutes.unlockStore(call) }
        get("/createStore") { pageRoutes.createStoreForm(call) }
        post("/createStore") { pageRoutes.createStore(call) }
        get("/store/{storeName}/delete") { pageRoutes.deleteStoreForm(call) }
        post("/store/{storeName}/delete") { pageRoutes.deleteStore(call) }

        get("/store/{storeName}/key/{key}/download") { pageRoutes.downloadEntry(call) }
        get("/store/{storeName}/key/{key}/view") { pageRoutes.viewEntry(call) }
        get("/store/{storeName}/key/{key}/edit") { pageRoutes.editEntry(call) }
        get("/store/{storeName}/key/{key}/editText") { pageRoutes.editTextEntry(call) }
        post("/store/{storeName}/key/{key}/editText") { pageRoutes.updateTextEntry(call) }
        get("/store/{storeName}/key/{key}/editFile") { pageRoutes.editFileEntry(call) }
        post("/store/{storeName}/key/{key}/editFile") { pageRoutes.updateFileEntry(call) }
        get("/store/{storeName}/createEntry") { pageRoutes.createEntry(call) }
        get("/store/{storeName}/createTextEntry") { pageRoutes.createTextEntryForm(call) }
        post("/store/{storeName}/createTextEntry") { pageRoutes.createTextEntry(call) }
        get("/store/{storeName}/createFileEntry") { pageRoutes.createFileEntryForm(call) }
        post("/store/{storeName}/createFileEntry") { pageRoutes.createFileEntry(call) }
        get("/store/{storeName}/key/{key}/deleteEntry") { pageRoutes.deleteEntry(call) }
    }
}

private fun Application.configureJson() {
    install(ContentNegotiation) { json() }
}

private fun Application.configureSession() {
    install(Sessions) {
        cookie<OpenedStores>("openedStore", SessionStorageMemoryWithEviction(10.minutes))
    }
}

private fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnauthorizedException> { call, cause ->
            if (call.request.contentType() == ContentType.Application.Json) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respondHtml(HttpStatusCode.Unauthorized) { unauthorizedPage() }
            }
        }
        exception<ValidationException> { call, cause ->
            if (call.request.contentType() == ContentType.Application.Json) {
                call.respond(HttpStatusCode.BadRequest, cause.errors)
            } else {
                call.respondHtml { defaultErrorPage() }
            }
        }
    }
}

private fun Application.configureDatabase() {
    val dbFile = System.getenv("DATA_FILE")
        ?: System.getenv("DATA_DIR")?.let { "$it/store.db" }
        ?: "store.db"

    println("Storing data in $dbFile")
    println("If you want to specify a specific folder or file, please use environment variable 'DATA_FILE' or 'DATA_DIR'")

    Database.connect("jdbc:sqlite:$dbFile")

    transaction {
        SchemaUtils.createMissingTablesAndColumns(StoreTable, EntryTable)
        exec("PRAGMA foreign_keys = ON")
    }
}
