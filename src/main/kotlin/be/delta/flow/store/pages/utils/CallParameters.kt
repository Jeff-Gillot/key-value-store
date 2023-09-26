package be.delta.flow.store.pages.utils

import be.delta.flow.store.model.Key
import be.delta.flow.store.model.StoreName
import io.ktor.server.application.*

val ApplicationCall.storeName: StoreName
    get() = StoreName.parse(parameters["storeName"]).value

val ApplicationCall.key: Key
    get() = Key.parse(parameters["key"]).value

val ApplicationCall.selectedKey: Key?
    get() = parameters["selectedKey"]?.let { Key.parse(it).valueOrNull }

val ApplicationCall.selectedStore: StoreName?
    get() = parameters["selectedStore"]?.let { StoreName.parse(it).valueOrNull }
