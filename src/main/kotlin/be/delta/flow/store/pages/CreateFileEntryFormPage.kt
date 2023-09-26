package be.delta.flow.store.pages

import be.delta.flow.store.model.Store
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*

fun HTML.createFileEntryFormPage(
    store: Store,
    errors: List<ValidationError> = emptyList(),
    key: String? = null,
    type: String? = null,
) {
    val remainingErrors = errors.toMutableList()
    val keyErrors = remainingErrors.extract { it.path == "key" }
    val typeErrors = remainingErrors.extract { it.path == "type" }
    val contentErrors = remainingErrors.extract { it.path == "content" }
    defaultPage("New entry") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb(store.name.toString(), "/store/${store.name}")
            breadcrumb("New Entry", "/store/${store.name}/createFileEntry")
        }
        h1 { +"New entry in ${store.name}" }
        createModeSelection(store, EditMode.File)
        form(method = FormMethod.post, encType = FormEncType.multipartFormData) {
            div {
                formInputKey(keyErrors, key)
                formInputType(typeErrors, type)
                formInputContentFile(contentErrors)
                formErrorFeedback(remainingErrors)
            }
            div(classes = "mt-3") {
                button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
                a(classes = "btn btn-danger ms-2", href = "/store/${store.name}") { +"Cancel" }
            }
        }
    }
}