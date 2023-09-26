package be.delta.flow.store.pages

import be.delta.flow.store.model.Entry
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*

fun HTML.editFileEntryPage(
    entry: Entry,
    errors: List<ValidationError> = emptyList(),
    type: String = entry.type.value.toString(),
) {
    val remainingErrors = errors.toMutableList()
    val typeErrors = remainingErrors.extract { it.path == "type" }
    val contentErrors = remainingErrors.extract { it.path == "content" }
    defaultPage("Edit entry") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb(entry.storeName.toString(), "/store/${entry.storeName}")
            breadcrumb(entry.key.toString(), "/store/${entry.storeName}/key/${entry.key}/editFile")
        }
        h1 { +"Edit entry ${entry.key} in ${entry.storeName}" }
        editModeSelection(entry, EditMode.File)
        form(method = FormMethod.post, encType = FormEncType.multipartFormData) {
            div {
                formInputType(typeErrors, type)
                formInputContentFile(contentErrors)
                formErrorFeedback(remainingErrors)
            }
            div(classes = "mt-3") {
                button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                a(classes = "btn btn-danger ms-2", href = "/store/${entry.storeName}") { +"Cancel" }
            }
        }
    }
}