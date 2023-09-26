package be.delta.flow.store.pages.components

import be.delta.flow.store.model.Entry
import be.delta.flow.store.model.Store
import kotlinx.html.*

fun FlowContent.editModeSelection(entry: Entry, editMode: EditMode) {
    div(classes = "btn-group  mb-2") {
        a("/store/${entry.storeName}/key/${entry.key}/edit?mode=text", classes = "btn") {
            classes += if (editMode == EditMode.Text) "btn-primary" else "btn-outline-primary"
            +"Text"
        }
        a("/store/${entry.storeName}/key/${entry.key}/edit?mode=file", classes = "btn") {
            classes += if (editMode == EditMode.File) "btn-primary" else "btn-outline-primary"
            +"File"
        }
    }
}


fun FlowContent.createModeSelection(store: Store, editMode: EditMode) {
    div(classes = "btn-group  mb-2") {
        a("/store/${store.name}/createTextEntry", classes = "btn") {
            classes += if (editMode == EditMode.Text) "btn-primary" else "btn-outline-primary"
            +"Text"
        }
        a("/store/${store.name}/createFileEntry", classes = "btn") {
            classes += if (editMode == EditMode.File) "btn-primary" else "btn-outline-primary"
            +"File"
        }
    }
}

enum class EditMode {
    Text,
    File
}