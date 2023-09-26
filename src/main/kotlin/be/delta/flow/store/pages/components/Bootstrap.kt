package be.delta.flow.store.pages.components

import kotlinx.html.BODY
import kotlinx.html.script

fun BODY.scripts() {
    script(src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js") {
        integrity = "sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        attributes["crossorigin"] = "anonymous"
    }
    script(src = "https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js") {
        attributes["crossorigin"] = "anonymous"
    }
}