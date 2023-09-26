package be.delta.flow.store.pages.components

import be.delta.flow.store.model.Type
import be.delta.flow.store.parsing.ValidationError
import kotlinx.html.*


fun FlowContent.formErrorFeedback(errors: List<ValidationError>) {
    if (errors.isNotEmpty()) {
        div(classes = "text-danger-emphasis mt-1") {
            errors.forEachIndexed { index, error ->
                +error.errorMessage
                if (index != errors.lastIndex) {
                    br()
                }
            }
        }
    }
}

fun CommonAttributeGroupFacade.addErrorClassIfNeeded(errors: List<ValidationError>) {
    if (errors.isNotEmpty()) {
        classes += "is-invalid"
    }
}

fun FlowContent.formInputType(
    typeErrors: List<ValidationError>,
    currentValue: String?
) {
    label(classes = "form-label mt-3") {
        attributes["for"] = "type"
        +"Type"
    }
    input(classes = "form-control", type = InputType.text) {
        id = "type"
        name = "type"
        addErrorClassIfNeeded(typeErrors)
        value = currentValue ?: ""
        attributes += "list" to "contentTypes"
    }
    dataList {
        id = "contentTypes"
        Type.availableTypes.forEach {
            option { value = it.value.toString() }
        }
    }
    formErrorFeedback(typeErrors)
}

fun FlowContent.formInputContentText(
    contentErrors: List<ValidationError>,
    content: String?,
) {
    label(classes = "form-label mt-3") {
        attributes["for"] = "content"
        +"Content"
    }
    textArea(classes = "form-control") {
        id = "content"
        name = "content"
        rows = "${((content ?: "").lines().size + 3).coerceAtMost(25)}"
        addErrorClassIfNeeded(contentErrors)
        +(content ?: "")
    }
    formErrorFeedback(contentErrors)
}

fun FlowContent.formInputKey(
    keyErrors: List<ValidationError>,
    key: String?
) {
    label(classes = "form-label mt-3") {
        attributes["for"] = "key"
        +"Key"
    }
    input(classes = "form-control", type = InputType.text) {
        name = "key"
        addErrorClassIfNeeded(keyErrors)
        value = key ?: ""
    }
    formErrorFeedback(keyErrors)
}

fun FlowContent.formInputContentFile(contentErrors: List<ValidationError>) {
    label(classes = "form-label mt-3") {
        attributes["for"] = "content"
        +"Content"
    }
    input(type = InputType.file, classes = "form-control") {
        id = "content"
        name = "content"
        addErrorClassIfNeeded(contentErrors)
    }
    formErrorFeedback(contentErrors)
}