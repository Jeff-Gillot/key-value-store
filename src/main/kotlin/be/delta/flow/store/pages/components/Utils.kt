package be.delta.flow.store.pages.components

fun String.limitedTo(size: Int): String {
    return if (length > size) {
        take(size - 3) + "..."
    } else {
        this
    }
}

fun String?.trimToNull(): String? {
    if (isNullOrBlank()) {
        return null
    }
    return trim()
}

fun <T> MutableList<T>.extract(predicate: (T) -> Boolean): List<T> {
    val result = filter(predicate)
    removeIf(predicate)
    return result
}