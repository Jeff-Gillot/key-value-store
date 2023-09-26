package be.delta.flow.store.pages

import be.delta.flow.store.pages.components.defaultPage
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.h1

fun HTML.unauthorizedPage() {
    defaultPage("Unauthorized") {
        h1 { +"You are not authorized to access this page!" }
        a(classes = "btn btn-primary", href = "/") { +"Home" }
    }
}

fun HTML.defaultErrorPage() {
    defaultPage("A server error occurred") {
        h1 { +"The server had an unexpected error, please retry or return to the home page" }
        a(classes = "btn btn-primary", href = "/") { +"Home" }
    }
}