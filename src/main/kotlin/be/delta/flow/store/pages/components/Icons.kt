package be.delta.flow.store.pages.components

import kotlinx.html.FlowContent
import kotlinx.html.i

fun FlowContent.downloadIcon(): Unit = icon("fa-download", family = "fa-solid")
fun FlowContent.floppyDiskIcon(): Unit = icon("fa-floppy-disk", family = "fa-solid")
fun FlowContent.eyeIcon(): Unit = icon("fa-eye", family = "fa-solid")
fun FlowContent.lockIcon(): Unit = icon("fa-lock", family = "fa-solid")
fun FlowContent.lockOpenIcon(): Unit = icon("fa-lock-open", family = "fa-solid")
fun FlowContent.penToSquareIcon(): Unit = icon("fa-pen-to-square", family = "fa-solid")
fun FlowContent.plusIcon(): Unit = icon("fa-plus", family = "fa-solid")
fun FlowContent.rotateLeftIcon(): Unit = icon("fa-rotate-left", family = "fa-solid")
fun FlowContent.trashIcon(): Unit = icon("fa-trash", family = "fa-solid")

private fun FlowContent.icon(icon: String, family: String) {
    i(classes = "$family $icon")
}