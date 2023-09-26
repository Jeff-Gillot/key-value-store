package be.delta.flow.store.pages

import be.delta.flow.store.model.*
import be.delta.flow.store.pages.components.EditMode
import be.delta.flow.store.pages.utils.*
import be.delta.flow.store.parsing.*
import be.delta.flow.store.routes.UnauthorizedException
import be.delta.flow.store.storage.PageStorage
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.transactions.transaction

class PageRoutes(
    private val storage: PageStorage
) {
    suspend fun home(call: ApplicationCall) {
        val stores: List<Store> = transaction { storage.listStores() }
        val openedStores: Set<StoreName> = call.openedStores
        val selectedStore: StoreName? = call.selectedStore

        call.respondHtml {
            storeListPage(stores, openedStores, selectedStore)
        }
    }

    suspend fun viewStore(call: ApplicationCall) {
        val storeName: StoreName = call.storeName
        val store: Store = transaction { storage.getStore(storeName) }
        val selectedKey = call.selectedKey

        if (call.isStoreOpen(store)) {
            val entries: List<Entry> = storage.getStoreEntries(store.name)
            call.respondHtml {
                storePage(store, entries, selectedKey)
            }
        } else {
            call.respondHtml {
                storePasswordFormPage(store)
            }
        }
    }

    suspend fun unlockStore(call: ApplicationCall) {
        val formParameters: Parameters = call.receiveParameters()
        val storeName: StoreName = call.storeName
        val store: Store = transaction { storage.getStore(storeName) }
        val storeSecret = StoreSecret.parseNullable(formParameters["password"])

        if (storeSecret.isSuccess && store.secret == storeSecret.value) {
            call.addOpenedStore(store.name)
            call.respondRedirect("/store/${store.name}")
        } else {
            call.respondHtml {
                storePasswordFormPage(store, listOf(invalidPasswordError))
            }
        }
    }

    suspend fun downloadEntry(call: ApplicationCall) {
        val entry: Entry = extractEntry(call)

        val contentDisposition = ContentDisposition
            .Attachment
            .withParameter(ContentDisposition.Parameters.FileName, entry.key.value)

        call.response.header(HttpHeaders.ContentDisposition, contentDisposition.toString())
        call.respondBytes(entry.content.byteArray, entry.type.value, HttpStatusCode.Accepted)
    }

    suspend fun viewEntry(call: ApplicationCall) {
        val entry = extractEntry(call)
        call.respondBytes(entry.content.byteArray, entry.type.value, HttpStatusCode.Accepted)
    }

    suspend fun editEntry(call: ApplicationCall) {
        val entry = extractEntry(call)
        val mode = extractEditMode(call, entry)

        when (mode) {
            EditMode.Text -> call.respondRedirect("/store/${entry.storeName}/key/${entry.key}/editText")
            EditMode.File -> call.respondRedirect("/store/${entry.storeName}/key/${entry.key}/editFile")
        }
    }

    suspend fun editTextEntry(call: ApplicationCall) {
        val entry = extractEntry(call)

        call.respondHtml {
            editTextEntryPage(entry)
        }
    }

    suspend fun updateTextEntry(call: ApplicationCall) {
        val entry = extractEntry(call)
        val parameters = call.receiveParameters()

        val type = Type.parse(parameters["type"])
        val content = Content.parse(parameters["content"])

        if (hasError(type, content)) {
            call.respondHtml {
                editTextEntryPage(
                    entry = entry,
                    type = parameters["type"] ?: "",
                    content = parameters["content"] ?: "",
                    errors = groupErrors(type, content).errors
                )
            }
        } else {
            storage.updateEntry(entry.copy(type = type.value, content = content.value))
            call.respondRedirect("/store/${entry.storeName}?selectedKey=${entry.key}#${entry.key}")
        }
    }

    suspend fun editFileEntry(call: ApplicationCall) {
        val entry = extractEntry(call)

        call.respondHtml {
            editFileEntryPage(entry)
        }
    }

    suspend fun updateFileEntry(call: ApplicationCall) {
        val entry = extractEntry(call)
        val parts = call.receiveMultipart()
        var content: ParsingResult<Content> = parseSingleValue<Content?>(null, "content").isNotNull()
        var type: ParsingResult<Type> = parseSingleValue<Type?>(null, "type").isNotNull()

        parts.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> content = Content.parse(part.streamProvider().readBytes())
                is PartData.FormItem -> when (part.name) {
                    "type" -> type = Type.parse(part.value)
                }

                else -> {}
            }
            part.dispose()
        }

        if (hasError(content, type)) {
            call.respondHtml {
                editFileEntryPage(
                    entry = entry,
                    type = type.valueOrNull?.value?.toString() ?: "",
                    errors = groupErrors(type, content).errors
                )
            }
        } else {
            storage.updateEntry(entry.copy(type = type.value, content = content.value))
            call.respondRedirect("/store/${entry.storeName}?selectedKey=${entry.key}#${entry.key}")
        }
    }

    suspend fun createStoreForm(call: ApplicationCall) {
        call.respondHtml {
            createStoreFormPage()
        }
    }

    suspend fun createStore(call: ApplicationCall) {
        val parameters = call.receiveParameters()

        val storeName = StoreName.parse(parameters["storeName"])
        val storeSecret = StoreSecret.parseNullable(parameters["storeSecret"])
        val store = storeName.valueOrNull?.let { storage.getStoreOrNull(it) }
        val storeExists = parseSingleValue(store, "storeName").isNull { "Store ${it.name} already exists" }

        if (hasError(storeName, storeSecret, storeExists)) {
            call.respondHtml {
                createStoreFormPage(
                    errors = groupErrors(storeName, storeSecret, storeExists).errors,
                    storeName = parameters["storeName"],
                    storeSecret = parameters["storeSecret"]
                )
            }
        } else {
            storage.createNewStore(Store(storeName.value, storeSecret.value))
            call.respondRedirect("/?selectedStore=$storeName#$storeName")
        }
    }

    suspend fun deleteStoreForm(call: ApplicationCall) {
        val storeName = call.storeName
        val store = storage.getStore(storeName)

        call.respondHtml {
            deleteStoreFormPage(store)
        }
    }

    suspend fun deleteStore(call: ApplicationCall) {
        val storeName = call.storeName
        val parameters = call.receiveParameters()
        val storeSecret = StoreSecret.parseNullable(parameters["storeSecret"])
        val store = storage.getStore(storeName)

        if (storeSecret.isFailure || store.secret != storeSecret.value) {
            call.respondHtml {
                deleteStoreFormPage(store, listOf(invalidPasswordError))
            }
        } else {
            storage.deleteStore(storeName)
            call.respondRedirect("/")
        }
    }

    suspend fun createEntry(call: ApplicationCall) {
        val store = storage.getStore(call.storeName)
        val mode = extractEditMode(call)

        if (call.isStoreOpen(store)) {
            when (mode) {
                EditMode.Text -> call.respondRedirect("/store/${store.name}/createTextEntry")
                EditMode.File -> call.respondRedirect("/store/${store.name}/createFileEntry")
            }
        } else {
            throw UnauthorizedException("Store is not open")
        }
    }

    suspend fun createTextEntryForm(call: ApplicationCall) {
        val store = storage.getStore(call.storeName)

        if (call.isStoreOpen(store)) {
            call.respondHtml {
                createTextEntryFormPage(store)
            }
        } else {
            throw UnauthorizedException("Store is not open")
        }
    }

    suspend fun createTextEntry(call: ApplicationCall) {
        val storeName = call.storeName
        val store = storage.getStore(storeName)

        val parameters = call.receiveParameters()
        val key = Key.parse(parameters["key"])
        val type = Type.parse(parameters["type"])
        val content = Content.parse(parameters["content"])
        val existingEntry = parseSingleValue(key.valueOrNull?.let { storage.getEntryOrNull(storeName, it) }, "key")
            .isNull { "${it.key} already exists" }


        if (!call.isStoreOpen(store)) {
            throw UnauthorizedException("Store is not open")
        } else if (hasError(key, type, content, existingEntry)) {
            call.respondHtml {
                createTextEntryFormPage(
                    store,
                    groupErrors(key, type, content, existingEntry).errors,
                    parameters["key"],
                    parameters["type"] ?: "text/plain",
                    parameters["content"]
                )
            }
        } else {
            val entry = Entry(key.value, storeName, type.value, content.value)
            storage.createEntry(entry)
            call.respondRedirect("/store/${store.name}?selectedKey=${entry.key}#${entry.key}")
        }
    }

    suspend fun createFileEntryForm(call: ApplicationCall) {
        val store = storage.getStore(call.storeName)

        if (call.isStoreOpen(store)) {
            call.respondHtml {
                createFileEntryFormPage(store)
            }
        } else {
            throw UnauthorizedException("Store is not open")
        }
    }

    suspend fun createFileEntry(call: ApplicationCall) {
        val store = storage.getStore(call.storeName)
        val parts = call.receiveMultipart()
        var content: ParsingResult<Content> = parseSingleValue<Content?>(null, "content").isNotNull()
        var type: ParsingResult<Type> = parseSingleValue<Type?>(null, "type").isNotNull()
        var key: ParsingResult<Key> = parseSingleValue<Key?>(null, "key").isNotNull()
        val existingEntry = parseSingleValue(key.valueOrNull?.let { storage.getEntryOrNull(store.name, it) }, "key")
            .isNull { "${it.key} already exists" }

        parts.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> content = Content.parse(part.streamProvider().readBytes())
                is PartData.FormItem -> when (part.name) {
                    "type" -> type = Type.parse(part.value)
                    "key" -> key = Key.parse(part.value)
                }

                else -> {}
            }
            part.dispose()
        }


        if (!call.isStoreOpen(store)) {
            throw UnauthorizedException("Store is not open")
        } else if (hasError(content, type, key, existingEntry)) {
            call.respondHtml {
                createFileEntryFormPage(
                    store = store,
                    key = key.valueOrNull?.value ?: "",
                    type = type.valueOrNull?.value?.toString() ?: "",
                    errors = groupErrors(type, content, key, existingEntry).errors
                )
            }
        } else {
            val newEntry = Entry(key.value, store.name, type.value, content.value)
            storage.createEntry(newEntry)
            call.respondRedirect("/store/${newEntry.storeName}?selectedKey=${newEntry.key}#${newEntry.key}")
        }
    }

    suspend fun deleteEntry(call: ApplicationCall) {
        val store = storage.getStore(call.storeName)
        val key = call.key

        if (call.isStoreOpen(store)) {
            storage.deleteEntry(store.name, key)
            call.respondRedirect("/store/${store.name}")
        } else {
            throw UnauthorizedException("Store is not open")
        }
    }

    private fun extractEntry(call: ApplicationCall): Entry {
        val store = storage.getStore(call.storeName)

        if (store.secret != null && !call.isStoreOpen(store)) {
            throw UnauthorizedException("Store is not open")
        }

        return storage.getEntry(store.name, call.key)
    }

    private fun extractEditMode(call: ApplicationCall, entry: Entry? = null): EditMode = when (call.parameters["mode"]) {
        "text" -> EditMode.Text
        "file" -> EditMode.File
        else -> when {
            entry == null -> EditMode.Text
            entry.type.value.match(ContentType.Text.Any) -> EditMode.Text
            entry.type.value.match(ContentType.Application.Json) -> EditMode.Text
            entry.content.isProbablyText() -> EditMode.Text
            else -> EditMode.File
        }
    }

    private val invalidPasswordError = ValidationError("storeSecret", "invalidPassword", "Wrong password")
}