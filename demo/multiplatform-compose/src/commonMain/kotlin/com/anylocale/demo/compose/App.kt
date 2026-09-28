package com.anylocale.demo.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import anylocale_android.demo.multiplatform_compose.generated.resources.Res
import anylocale_android.demo.multiplatform_compose.generated.resources.description
import anylocale_android.demo.multiplatform_compose.generated.resources.percentage_placeholder
import anylocale_android.demo.multiplatform_compose.generated.resources.plr_test_placeholder_2
import com.anylocale.Anylocale
import com.anylocale.pluralStringResource
import com.anylocale.stringResource

// import org.jetbrains.compose.resources.stringResource

@Composable
fun App() {
    // no remember required, using a singleton
    val anylocale = Anylocale.instance

    MaterialTheme {
        Column {
            // Use anylocale version of stringResource composable
            // (or alternatively enable anylocale compiler plugin which will convert the calls automatically)
            Text(text = stringResource(Res.string.description))
            Text(text = stringResource(Res.string.percentage_placeholder, "87"))
            Text(text = pluralStringResource(Res.plurals.plr_test_placeholder_2, 2, 10, "Plurals"))
            Button(
                onClick = {
                    anylocale.setLocale("en")
                }
            ) {
                Text(text = "English")
            }
            Button(
                onClick = {
                    anylocale.setLocale("fr")
                }
            ) {
                Text(text = "Français")
            }
            Button(
                onClick = {
                    anylocale.setLocale("cs")
                }
            ) {
                Text(text = "Čeština")
            }
        }
    }
}
