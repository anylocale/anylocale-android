package com.anylocale.demo.compose

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    Setup.init()

    singleWindowApplication(
        title = "Anylocale Compose Multiplatform Demo"
    ) {
        App()
    }
}