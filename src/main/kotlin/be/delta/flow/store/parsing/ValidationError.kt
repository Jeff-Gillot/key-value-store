package be.delta.flow.store.parsing

data class ValidationError(
    val path: String,
    val errorCode: String,
    val errorMessage: String,
) {
    override fun toString(): String {
        return when {
            path.isEmpty() -> "$errorCode -> $errorMessage"
            else -> "$path: $errorCode -> $errorMessage"
        }
    }
}