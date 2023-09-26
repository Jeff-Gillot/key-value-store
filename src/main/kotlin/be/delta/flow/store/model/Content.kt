package be.delta.flow.store.model

import be.delta.flow.store.parsing.*
import java.util.regex.Pattern

data class Content(val byteArray: ByteArray) {

    @Suppress("RegExpRepeatedSpace", "RegExpUnexpectedAnchor")
    fun isProbablyText(): Boolean {
        val pattern = Pattern.compile(
            """\A(
                        [\x09\x0A\x0D\x20-\x7E]            # ASCII
                      | [\xC2-\xDF][\x80-\xBF]             # non-overlong 2-byte
                      | \xE0[\xA0-\xBF][\x80-\xBF]         # excluding overlongs
                      | [\xE1-\xEC\xEE\xEF][\x80-\xBF]{2}  # straight 3-byte
                      | \xED[\x80-\x9F][\x80-\xBF]         # excluding surrogates
                      | \xF0[\x90-\xBF][\x80-\xBF]{2}      # planes 1-3
                      | [\xF1-\xF3][\x80-\xBF]{3}          # planes 4-15
                      | \xF4[\x80-\x8F][\x80-\xBF]{2}      # plane 16
                      )*\z
                      """,
            Pattern.COMMENTS
        ).toRegex()

        return String(byteArray).matches(pattern)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Content

        return byteArray.contentEquals(other.byteArray)
    }

    override fun hashCode(): Int {
        return byteArray.contentHashCode()
    }

    companion object {
        fun ByteArray.toContent(): Content = Content(this)

        fun parse(value: String?): ParsingResult<Content> = parseSingleValue(value, "content")
            .isNotNull { "Content should not be empty" }
            .isNotBlank { "Content should not be empty" }
            .map { Content(it.toByteArray()) }

        fun parse(value: ByteArray?): ParsingResult<Content> = parseSingleValue(value, "content")
            .isNotNull { "Content should not be empty" }
            .isNotEmpty { "Content should not be empty" }
            .map { Content(it) }
    }
}