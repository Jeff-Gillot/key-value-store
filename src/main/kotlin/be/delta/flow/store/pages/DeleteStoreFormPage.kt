package be.delta.flow.store.pages

import be.delta.flow.store.model.Store
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*
import kotlin.text.Typography.nbsp

fun HTML.deleteStoreFormPage(
    store: Store,
    errors: List<ValidationError> = emptyList(),
) {
    val remainingErrors = errors.toMutableList()
    val storeSecretErrors = remainingErrors.extract { it.path == "storeSecret" }
    defaultPage("Delete store") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb(store.name.toString(), "/store/${store.name}")
            breadcrumb("Delete", "/store/${store.name}/delete")
        }
        h4 {
            +"Are you sure you want to delete the store "
            b { +store.name.toString() }
            +" ?"
        }
        p{
            +"All the data in this store will be deleted"
            br()
            +"This operation is irreversible"
        }
        form(method = FormMethod.post) {
            if (store.secret != null) {
                div(classes = "mb-3") {
                    label(classes = "form-label") {
                        attributes["for"] = "storeSecret"
                        +"Please enter the store password"
                    }
                    input(classes = "form-control mb-3", type = InputType.password) {
                        id = "storeSecret"
                        name = "storeSecret"
                        addErrorClassIfNeeded(storeSecretErrors)
                    }
                    formErrorFeedback(storeSecretErrors)
                }
            }
            formErrorFeedback(remainingErrors)
            button(classes = "btn btn-danger", type = ButtonType.submit) { trashIcon(); +"${nbsp}Delete" }
            a(classes = "btn btn-success ms-2", href = "/?storeName=${store.name}#${store.name}") { rotateLeftIcon(); +"${nbsp}Cancel" }
        }
    }
}