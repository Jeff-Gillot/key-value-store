package be.delta.flow.store.model

import be.delta.flow.store.parsing.*

@JvmInline
value class Key private constructor(val value: String) {
    override fun toString(): String = value

    companion object {
        fun String.toKey(): Key =
            parse(this).value

        fun parse(value: String?): ParsingResult<Key> =
            parseSingleValue(value, "key")
                .trimToNull()
                .isNotNull()
                .hasMaxLength(1024)
                .map { Key(it) }
    }
}