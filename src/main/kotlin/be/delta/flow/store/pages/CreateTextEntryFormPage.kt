package be.delta.flow.store.pages

import be.delta.flow.store.model.Store
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*

fun HTML.createTextEntryFormPage(
    store: Store,
    errors: List<ValidationError> = emptyList(),
    key: String? = null,
    type: String? = null,
    content: String? = null,
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
            breadcrumb("New Entry", "/store/${store.name}/createTextEntry")
        }
        h1 { +"New entry in ${store.name}" }
        createModeSelection(store, EditMode.Text)
        form(method = FormMethod.post) {
            div(classes = "mb-3") {
                formInputKey(keyErrors, key)
                formInputType(typeErrors, type)
                formInputContentText(contentErrors, content)
                formErrorFeedback(remainingErrors)
            }
            button(classes = "btn btn-primary", type = ButtonType.submit) { +"Save" }
            a(classes = "btn btn-danger ms-2", href = "/store/${store.name}") { +"Cancel" }
        }
    }
}