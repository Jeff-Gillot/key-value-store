package be.delta.flow.store.pages.components

import kotlinx.html.*

fun HTML.defaultBody(block: BODY.() -> Unit) {
    body {
        block.invoke(this)
        scripts()
    }
}


fun HTML.defaultPage(windowTitle: String, block: DIV.() -> Unit) {
    lang = "en"
    attributes["data-bs-theme"] = "dark"
    defaultHead(windowTitle)
    defaultBody {
        div(classes = "container mt-2") {
            block.invoke(this)
        }
    }
}