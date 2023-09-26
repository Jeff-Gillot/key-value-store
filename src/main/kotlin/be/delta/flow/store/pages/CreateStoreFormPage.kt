package be.delta.flow.store.pages

import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*
import kotlin.text.Typography.nbsp

fun HTML.createStoreFormPage(
    errors: List<ValidationError> = emptyList(),
    storeName: String? = null,
    storeSecret: String? = null
) {
    val remainingErrors = errors.toMutableList()
    val storeNameErrors = remainingErrors.extract { it.path == "storeName" }
    val storeSecretErrors = remainingErrors.extract { it.path == "storeSecret" }
    defaultPage("New store") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb("New", "/createStore")

        }
        h1 { +"New store" }
        form(method = FormMethod.post) {
            div {
                label(classes = "form-label") {
                    attributes["for"] = "storeName"
                    +"Store Name"
                }
                input(classes = "form-control", type = InputType.text) {
                    id = "storeName"
                    name = "storeName"
                    addErrorClassIfNeeded(storeNameErrors)
                    value = storeName ?: ""
                }
                formErrorFeedback(storeNameErrors)
                label(classes = "form-label mt-3") {
                    attributes["for"] = "storeSecret"
                    +"Password"
                }
                input(classes = "form-control", type = InputType.password) {
                    id = "storeSecret"
                    name = "storeSecret"
                    placeholder = "Password is optional"
                    addErrorClassIfNeeded(storeSecretErrors)
                    value = storeSecret ?: ""
                }
                formErrorFeedback(storeSecretErrors)
            }
            formErrorFeedback(remainingErrors)
            div(classes = "mt-3") {
                button(classes = "btn btn-success", type = ButtonType.submit) { floppyDiskIcon(); +"${nbsp}Create" }
                a(classes = "btn btn-danger ms-2", href = "/") { rotateLeftIcon(); +"${nbsp}Cancel" }
            }
        }
    }
}