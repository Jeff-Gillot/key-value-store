package be.delta.flow.store.pages

import be.delta.flow.store.model.Store
import be.delta.flow.store.model.StoreName
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumb
import be.delta.flow.store.pages.components.BreadCrumbs.breadcrumbs
import kotlinx.html.*
import kotlin.text.Typography.nbsp

fun HTML.storeListPage(stores: List<Store>, openedStores: Set<StoreName>, selectedStore: StoreName?) {
    defaultPage("Stores") {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
        }
        table(classes = "table table-dark table-striped table-hover") {
            thead {
                tr {
                    th { +"Store Name" }
                    th { +"Lock" }
                    th(classes = "text-end") { +"Actions" }
                }
            }
            tbody(classes = "table-group-divider") {
                stores.forEach { store ->
                    tr {
                        role = "button"
                        onClick = "window.location='/store/${store.name}'"
                        if (selectedStore == store.name) {
                            classes += "table-active"
                        }
                        td(classes = "align-middle") {
                            a(href = "#${store.name}")
                            +store.name.toString()
                        }
                        td(classes = "align-middle") {
                            when {
                                store.name in openedStores -> lockOpenIcon()
                                store.secret != null -> lockIcon()
                            }
                        }
                        td(classes = "text-end align-middle") {
                            a(classes = "btn btn-primary", href = "/store/${store.name}") { eyeIcon(); +"${nbsp}Open" }
                            a(classes = "btn btn-danger ms-2", href = "/store/${store.name}/delete") { trashIcon(); +"${nbsp}Delete" }
                        }
                    }
                }
            }
        }
        a(classes = "btn btn-success", href = "/createStore") { plusIcon(); +"${nbsp}New" }
    }
}
