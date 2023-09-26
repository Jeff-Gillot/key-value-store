package be.delta.flow.store.pages.components

import be.delta.flow.store.model.StoreName
import kotlinx.html.*

object BreadCrumbs {
    fun FlowContent.breadcrumbs(block: BREADCRUMBS.() -> Unit) {
        nav {
            ol(classes = "breadcrumb") {
                block.invoke(BREADCRUMBS(consumer))
            }
        }
    }

    fun BREADCRUMBS.breadcrumb(text: String, href: String? = null) {
        li(classes = "breadcrumb-item") {
            if (href != null) {
                a(href = href) { +text }
            } else {
                +text
            }
        }
    }

    class BREADCRUMBS(consumer: TagConsumer<*>) : OL(emptyMap(), consumer)

    fun DIV.storeBreadcrumbs(storeName: StoreName) {
        breadcrumbs {
            breadcrumb("Home", "/")
            breadcrumb("Stores", "/")
            breadcrumb(storeName.toString(), "/store/$storeName")
        }
    }
}
