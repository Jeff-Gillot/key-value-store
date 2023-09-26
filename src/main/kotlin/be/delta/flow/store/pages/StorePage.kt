package be.delta.flow.store.pages

import be.delta.flow.store.model.Entry
import be.delta.flow.store.model.Key
import be.delta.flow.store.model.Store
import be.delta.flow.store.pages.components.*
import be.delta.flow.store.pages.components.BreadCrumbs.storeBreadcrumbs
import kotlinx.html.*
import kotlin.text.Typography.nbsp

fun HTML.storePage(store: Store, entries: List<Entry>, selectedKey: Key?) {
    defaultPage("Store - ${store.name}") {
        storeBreadcrumbs(store.name)
        table(classes = "table table-dark table-striped table-hover") {
            thead {
                tr {
                    th { +"Key" }
                    th { +"Type" }
                    th { +"Content" }
                    th(classes = "text-end") { +"Actions" }
                }
            }
            tbody(classes = "table-group-divider") {
                entries.forEachIndexed { index, entry ->
                    tr {
                        role = "button"
                        onClick = "window.location='/store/${entry.storeName}/key/${entry.key}/edit'"
                        if (selectedKey == entry.key) {
                            classes += "table-active"
                        }
                        td(classes = "align-middle") {
                            a(href = "#${entry.key}")
                            +entry.key.toString()
                        }
                        td(classes = "align-middle") { +entry.type.toString() }
                        td(classes = "align-middle") {
                            if (entry.content.isProbablyText()) {
                                +String(entry.content.byteArray).limitedTo(50)
                            }
                        }
                        td(classes = "text-end align-middle") {
                            a(classes = "btn btn-primary", href = "/store/${entry.storeName}/key/${entry.key}/view") { eyeIcon(); +"${nbsp}View" }
                            a(classes = "btn btn-primary ms-2", href = "/store/${entry.storeName}/key/${entry.key}/download") { downloadIcon(); +"${nbsp}Download" }
                            a(classes = "btn btn-primary ms-2", href = "/store/${entry.storeName}/key/${entry.key}/edit") { penToSquareIcon(); +"${nbsp}Edit" }
                            a(classes = "btn btn-danger ms-2  collapse show") {
                                classes += "deleteButton-${index}"
                                onClick = "\$('.deleteButton-${index}').hide();\$('.confirmDeleteButton-${index}').show();event.stopPropagation()"
                                trashIcon()
                                +"${nbsp}Delete"
                            }
                            a(classes = "btn btn-danger ms-2 collapse", href = "/store/${entry.storeName}/key/${entry.key}/deleteEntry") {
                                classes += "confirmDeleteButton-${index}"
                                trashIcon()
                            }
                            a(classes = "btn btn-success ms-2 collapse") {
                                classes += "confirmDeleteButton-${index}"
                                onClick = "\$('.deleteButton-${index}').show();\$('.confirmDeleteButton-${index}').hide();event.stopPropagation()"
                                rotateLeftIcon()
                            }
                        }
                    }
                }
            }
        }
        a(classes = "btn btn-success ms-2", href = "/store/${store.name}/createEntry") { plusIcon(); +"${nbsp}New" }
    }

}
