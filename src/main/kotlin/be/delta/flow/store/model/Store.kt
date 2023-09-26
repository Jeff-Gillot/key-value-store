package be.delta.flow.store.model

data class Store(
    val name: StoreName,
    val secret: StoreSecret?
)