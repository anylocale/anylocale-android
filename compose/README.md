# anylocale SDK: compose module

`com.anylocale:sdk-compose`. Drop-in replacements for `stringResource`,
`pluralStringResource` and `stringArrayResource` for Jetpack Compose (Android resource ids)
and Compose Multiplatform (`Res.string`, `Res.plurals`, `Res.array`). Depends on and
re-exports `com.anylocale:sdk`.

The [top-level README](../README.md) covers installation, initialisation and the wire format.

## Setup

```toml
[libraries]
anylocale-compose = { group = "com.anylocale", name = "sdk-compose", version.ref = "anylocale" }
```

```kotlin
dependencies {
    implementation(libs.anylocale.compose)
}
```

Initialise `Anylocale` once at startup, in `Application.onCreate` on Android or before the
first composition elsewhere, as shown in the top-level README.

## Usage

Import from `com.anylocale`. Each composable returns the bundled resource first and
recomposes when the downloaded translation is available or the locale changes. When
`Anylocale` has not been initialised the composables fall through to the standard Compose
resource lookup.

Jetpack Compose:

```kotlin
import com.anylocale.pluralStringResource
import com.anylocale.stringArrayResource
import com.anylocale.stringResource

Text(stringResource(R.string.welcome_message))
Text(stringResource(R.string.welcome_user, name))
Text(pluralStringResource(R.plurals.item_count, count, count))
Text(stringArrayResource(R.array.options).joinToString())
```

Compose Multiplatform:

```kotlin
Text(stringResource(Res.string.welcome_message))
Text(stringResource(Res.string.welcome_user, name))
Text(pluralStringResource(Res.plurals.item_count, count, count))
Text(stringArrayResource(Res.array.options).joinToString())
```

Apple targets additionally get `stringResource(key, default, table)` backed by `NSBundle`.

### Explicit instance

Every composable has an overload whose first parameter is the `Anylocale` instance to use:

```kotlin
val anylocale = remember { Anylocale.new { contentDelivery { url = "..." } } }
Text(stringResource(anylocale, Res.string.welcome_message))
```

### Locale switching

```kotlin
@Composable
fun LocaleSwitcher() {
    val anylocale = Anylocale.instance
    val current = anylocale.changeFlow
        .mapLatest { anylocale.getLocale() }
        .collectAsState(initial = anylocale.getLocale())

    Row {
        Text(current.value.toTag("-"))
        Button(onClick = { anylocale.setLocale("en") }) { Text("English") }
        Button(onClick = { anylocale.setLocale("cs") }) { Text("Čeština") }
    }
}
```

`Anylocale.Config.Builder.locale(androidx.compose.ui.text.intl.Locale)` and
`Anylocale.setLocale(androidx.compose.ui.text.intl.Locale)` accept the Compose locale type.

## Troubleshooting

- Text never changes: confirm the import is `com.anylocale.stringResource`, not the
  `androidx` or `org.jetbrains` one, and that `Anylocale.init` ran before composition.
- Wrong key: the key is the resource entry name (`R.string.welcome_message` looks up
  `welcome_message`); the distribution has to contain that key.
