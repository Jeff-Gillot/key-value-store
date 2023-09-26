package be.delta.flow.store.model

import be.delta.flow.store.parsing.*

@JvmInline
value class StoreName private constructor(val value: String) {
    override fun toString(): String = value

    companion object {
        fun String.toStoreName(): StoreName =
            parse(this).value

        fun parse(value: String?): ParsingResult<StoreName> =
            parseSingleValue(value, "storeName")
                .trimToNull()
                .isNotNull()
                .hasMaxLength(200)
                .map { StoreName(it) }
    }
}