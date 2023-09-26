package be.delta.flow.store.pages

import be.delta.flow.store.model.Store
import be.delta.flow.store.pages.components.BreadCrumbs.storeBreadcrumbs
import be.delta.flow.store.pages.components.addErrorClassIfNeeded
import be.delta.flow.store.pages.components.defaultPage
import be.delta.flow.store.pages.components.extract
import be.delta.flow.store.pages.components.formErrorFeedback
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*
import kotlin.collections.set

fun HTML.storePasswordFormPage(
    store: Store,
    errors: List<ValidationError> = emptyList(),
) {
    val remainingErrors = errors.toMutableList()
    val storeSecretErrors = remainingErrors.extract { it.path == "storeSecret" }

    defaultPage("Store - ${store.name}") {
        storeBreadcrumbs(store.name)
        h1 { +"Open protected store" }
        form(method = FormMethod.post) {
            div {
                label(classes = "form-label") {
                    attributes["for"] = "password"
                    +"Password"
                }
                input(classes = "form-control", type = InputType.password) {
                    id = "password"
                    name = "password"
                    addErrorClassIfNeeded(storeSecretErrors)
                }
                formErrorFeedback(storeSecretErrors)
                formErrorFeedback(remainingErrors)
            }
            div(classes = "mt-3") {
                button(classes = "btn btn-primary", type = ButtonType.submit) { +"Unlock" }
                a(classes = "btn btn-secondary ms-2", href = "/") { +"Cancel" }
            }
        }
    }
}