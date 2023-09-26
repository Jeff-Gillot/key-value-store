package be.delta.flow.store.model

import be.delta.flow.store.parsing.*

@JvmInline
value class StoreSecret private constructor(val value: String) {
    override fun toString(): String = "*****"

    companion object {
        fun String.toStoreSecret(): StoreSecret =
            parse(this).value

        fun parse(value: String?) =
            parseNullable(value).isNotNull()

        fun parseNullable(value: String?): ParsingResult<StoreSecret?> =
            parseSingleValue(value, "storeSecret")
                .trimToNull()
                .isNullOr()
                ?.hasMaxLength(1024)
                ?.map { StoreSecret(it) }
                ?: ParsingSuccess(null)
    }
}