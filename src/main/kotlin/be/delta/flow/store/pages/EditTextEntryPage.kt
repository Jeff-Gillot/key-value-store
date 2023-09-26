package be.delta.flow.store.pages

import be.delta.flow.store.model.Entry
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*

fun HTML.editTextEntryPage(
    entry: Entry,
    errors: List<ValidationError> = emptyList(),
    type: String = entry.type.value.toString(),
    content: String = String(entry.content.byteArray)
) {
    val remainingErrors = errors.toMutableList()
    val typeErrors = remainingErrors.extract { it.path == "type" }
    val contentErrors = remainingErrors.extract { it.path == "content" }
    defaultPage("Edit entry - ${entry.key}") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb(entry.storeName.toString(), "/store/${entry.storeName}")
            breadcrumb(entry.key.toString(), "/store/${entry.storeName}/key/${entry.key}/editText")
        }
        h1 { +"Edit entry ${entry.key} in ${entry.storeName}" }
        editModeSelection(entry, EditMode.Text)
        form(method = FormMethod.post) {
            div {
                formInputType(typeErrors, type)
                formInputContentText(contentErrors, content)
                formErrorFeedback(remainingErrors)
            }
            div(classes = "mt-3") {
                button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                a(classes = "btn btn-danger ms-2", href = "/store/${entry.storeName}") { +"Cancel" }
            }
        }
    }
}
