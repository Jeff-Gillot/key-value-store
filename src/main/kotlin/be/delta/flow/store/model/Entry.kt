package be.delta.flow.store.model

data class Entry(
    val key: Key,
    val storeName: StoreName,
    val type: Type,
    val content: Content
)

