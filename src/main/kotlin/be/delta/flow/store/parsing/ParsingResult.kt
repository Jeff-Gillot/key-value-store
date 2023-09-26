package be.delta.flow.store.parsing

import io.ktor.http.*


sealed interface ParsingResult<T> {
    val isSuccess: Boolean
    val isFailure: Boolean
    val value: T
    val valueOrNull: T?
    val errors: List<ValidationError>
    val errorsOrNull: List<ValidationError>?
    val path: Path

    fun <R> map(block: (T) -> R): ParsingResult<R>
}

data class ParsingSuccess<T>(
    override val value: T,
    override val path: Path = Path.empty
) : ParsingResult<T> {
    override val isSuccess = true
    override val isFailure = false
    override val valueOrNull: T = value
    override val errors: List<ValidationError> get() = throw IllegalAccessError("This does not have errors")
    override val errorsOrNull: List<ValidationError>? = null

    override fun <R> map(block: (T) -> R): ParsingResult<R> =
        success(block.invoke(value))

    fun <R> failure(errorCode: String, errorMessage: String): ParsingFailure<R> =
        ParsingFailure<R>(listOf(ValidationError("$path", errorCode, errorMessage)), path)

    fun <R> success(value: R) = ParsingSuccess(value, path)
}

data class ParsingFailure<T>(
    override val errors: List<ValidationError>,
    override val path: Path = Path.empty
) : ParsingResult<T> {
    override val isSuccess = false
    override val isFailure = true
    override val value: T get() = throw ValidationException(errors)
    override val valueOrNull: T? = null
    override val errorsOrNull: List<ValidationError> = errors

    override fun <R> map(block: (T) -> R): ParsingResult<R> =
        failure()

    fun <R> failure(): ParsingFailure<R> = ParsingFailure<R>(errors, path)
}

fun <T> ParsingResult<T?>.isNotNull(errorCode: String = "notNull", errorMessage: () -> String = { "Value should not be null" }): ParsingResult<T> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value == null -> failure(errorCode, errorMessage.invoke())
        else -> ParsingSuccess(value!!, path)
    }
}


fun ParsingResult<ByteArray>.isNotEmpty(errorCode: String = "notEmpty", errorMessage: (ByteArray) -> String = { "'$value' should not be empty" }): ParsingResult<ByteArray> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value.isEmpty() -> failure(errorCode, errorMessage(value))
        else -> ParsingSuccess(value, path)
    }
}

fun ParsingResult<String>.isNotBlank(errorCode: String = "notBlank", errorMessage: () -> String = { "Value should not be empty" }): ParsingResult<String> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value.isBlank() -> failure(errorCode, errorMessage.invoke())
        else -> this
    }
}

fun ParsingResult<String>.isContentType(): ParsingResult<ContentType> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> {
        try {
            ParsingSuccess(ContentType.parse(value), path)
        } catch (throwable: Throwable) {
            failure("notContentType", throwable.message!!)
        }
    }
}


fun <T> ParsingResult<T?>.isNull(errorCode: String = "null", errorMessage: (T) -> String = { "'$value' should be null" }): ParsingResult<Unit> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value != null -> failure(errorCode, errorMessage(value!!))
        else -> ParsingSuccess(Unit, path)
    }
}

fun <T> ParsingResult<T?>.isNullOr(): ParsingResult<T>? = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value == null -> null
        else -> ParsingSuccess(value!!, path)
    }
}

fun ParsingResult<String>.isInteger(): ParsingResult<Int> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value.trim().all { it.isDigit() } -> success(value.toInt())
        else -> failure("isInteger", "'$value' should be an integer")
    }
}

fun ParsingResult<String>.hasMaxLength(maxLength: Int): ParsingResult<String> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value.length <= maxLength -> success(value)
        else -> failure("maxLength", "'$value' should have a maximum length of $maxLength but has a length of ${value.length}")
    }
}

fun ParsingResult<String?>.trimToNull(): ParsingResult<String?> = map { if (it.isNullOrBlank()) null else it.trim() }

fun ParsingResult<String>.hasLengthIn(range: IntRange): ParsingResult<String> = when (this) {
    is ParsingFailure -> failure()
    is ParsingSuccess -> when {
        value.length in range -> success(value)
        else -> failure("lengthRange", "'$value' should have a range between ${range.first} and ${range.last}")
    }
}

class ParserContext(val path: Path) {
    fun <T> parseValue(value: T) = ParsingSuccess(value, path)
    fun <T> parseValue(value: T, name: String) = ParsingSuccess(value, path + name)

    fun <T> withPath(name: String, block: ParserContext.() -> T): T =
        block.invoke(ParserContext(path + name))
}

fun <T> parseSingleValue(value: T) = ParsingSuccess(value, Path.empty)
fun <T> parseSingleValue(value: T, name: String) = ParsingSuccess(value, Path.empty + name)

fun <T> withParsingContext(block: ParserContext.() -> T): T = block.invoke(ParserContext(Path.empty))
fun <T> withParsingContext(name: String, block: ParserContext.() -> T): T = block.invoke(ParserContext(Path.empty + name))

fun <T> hasError(vararg parsingResults: ParsingResult<out T>): Boolean = hasError(parsingResults.toList())
fun <T> hasError(parsingResults: List<ParsingResult<out T>>): Boolean = parsingResults.any { it is ParsingFailure }
fun <T> groupErrors(vararg parsingResults: ParsingResult<out T>): ParsingFailure<Unit> = groupErrors(parsingResults.toList())
fun <T> groupErrors(parsingResults: List<ParsingResult<out T>>): ParsingFailure<Unit> = ParsingFailure(parsingResults.filterIsInstance<ParsingFailure<*>>().flatMap { it.errors }, Path.empty)

inline fun <T : Any> onErrors(vararg parsingResults: ParsingResult<out T>, block: (ParsingFailure<Unit>) -> Unit) {
    onErrors(parsingResults.toList(), block)
}

inline fun <T : Any> onErrors(parsingResults: List<ParsingResult<out T>>, block: (ParsingFailure<Unit>) -> Unit) {
    if (parsingResults.any { it.isFailure }) {
        block.invoke(groupErrors(parsingResults.toList()))
    }
}