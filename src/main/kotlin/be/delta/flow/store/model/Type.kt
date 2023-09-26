package be.delta.flow.store.model

import be.delta.flow.store.parsing.ParsingResult
import be.delta.flow.store.parsing.isContentType
import be.delta.flow.store.parsing.isNotNull
import be.delta.flow.store.parsing.parseSingleValue
import io.ktor.http.*

@JvmInline
value class Type private constructor(val value: ContentType) {
    override fun toString(): String = value.toString()

    companion object {
        fun String.toType(): Type = parse(this).value

        fun parse(value: String?): ParsingResult<Type> {
            return parseSingleValue(value, "type")
                .isNotNull()
                .isContentType()
                .map { Type(it) }
        }

        val availableTypes = listOf(
            Type(ContentType.Any),
            Type(ContentType.Application.Any),
            Type(ContentType.Application.Atom),
            Type(ContentType.Application.Cbor),
            Type(ContentType.Application.Json),
            Type(ContentType.Application.HalJson),
            Type(ContentType.Application.JavaScript),
            Type(ContentType.Application.OctetStream),
            Type(ContentType.Application.Rss),
            Type(ContentType.Application.Xml),
            Type(ContentType.Application.Xml_Dtd),
            Type(ContentType.Application.Zip),
            Type(ContentType.Application.GZip),
            Type(ContentType.Application.FormUrlEncoded),
            Type(ContentType.Application.Pdf),
            Type(ContentType.Application.Xlsx),
            Type(ContentType.Application.Docx),
            Type(ContentType.Application.Pptx),
            Type(ContentType.Application.ProtoBuf),
            Type(ContentType.Application.Wasm),
            Type(ContentType.Application.ProblemJson),
            Type(ContentType.Application.ProblemXml),
            Type(ContentType.Audio.Any),
            Type(ContentType.Audio.MP4),
            Type(ContentType.Audio.MPEG),
            Type(ContentType.Audio.OGG),
            Type(ContentType.Companion.Any),
            Type(ContentType.Font.Any),
            Type(ContentType.Font.Collection),
            Type(ContentType.Font.Otf),
            Type(ContentType.Font.Sfnt),
            Type(ContentType.Font.Ttf),
            Type(ContentType.Font.Woff),
            Type(ContentType.Font.Woff2),
            Type(ContentType.Image.Any),
            Type(ContentType.Image.GIF),
            Type(ContentType.Image.JPEG),
            Type(ContentType.Image.PNG),
            Type(ContentType.Image.SVG),
            Type(ContentType.Image.XIcon),
            Type(ContentType.Message.Any),
            Type(ContentType.Message.Http),
            Type(ContentType.Text.Any),
            Type(ContentType.Text.Plain),
            Type(ContentType.Text.CSS),
            Type(ContentType.Text.CSV),
            Type(ContentType.Text.Html),
            Type(ContentType.Text.JavaScript),
            Type(ContentType.Text.VCard),
            Type(ContentType.Text.Xml),
            Type(ContentType.Text.EventStream),
            Type(ContentType.Video.Any),
            Type(ContentType.Video.MPEG),
            Type(ContentType.Video.MP4),
            Type(ContentType.Video.OGG),
            Type(ContentType.Video.QuickTime),
        )
    }
}