# anylocale SDK: Gradle plugin

Plugin id `com.anylocale.sdk`, artifact `com.anylocale:sdk-gradle-plugin`. Registers the
`anylocale { }` extension and is the host for the Kotlin compiler plugin that rewrites
standard string lookups into their anylocale equivalents at compile time.

The compiler plugin (`com.anylocale:sdk-compiler-plugin`, id
`com.anylocale.sdk.compiler-plugin`) is currently disabled: it broke with Kotlin 2.2 and the
module is commented out in `settings.gradle.kts`. Applying the Gradle plugin today only adds
the extension; the runtime integrations in the `sdk` and `sdk-compose` modules
(`AnylocaleContextWrapper`, the `com.anylocale.stringResource` composables) do not need it.

## Setup

```toml
[plugins]
anylocale = { id = "com.anylocale.sdk", version.ref = "anylocale" }
```

```kotlin
plugins {
    alias(libs.plugins.anylocale)
}
```

## Configuration

```kotlin
anylocale {
    compilerPlugin {
        android {
            replaceGetString.set(true)          // Context.getString -> getStringT, default true
            replacePluralString.set(true)       // Resources.getQuantityString -> getQuantityStringT, default true
        }
        compose {
            replaceStringResource.set(true)         // stringResource -> com.anylocale.stringResource, default true
            replacePluralStringResource.set(true)   // pluralStringResource -> com.anylocale.pluralStringResource, default true
        }
    }
}
```

The values are passed to the compiler plugin as `anylocale.android.getString`,
`anylocale.android.pluralString`, `anylocale.compose.stringResource` and
`anylocale.compose.pluralStringResource`.

## What the compiler plugin rewrites

Android, any `Context` or `Resources` subtype:

```kotlin
context.getString(R.string.welcome_message)                    // -> context.getStringT(R.string.welcome_message)
context.getString(R.string.welcome_user, username)             // -> context.getStringT(R.string.welcome_user, username)
resources.getQuantityString(R.plurals.item_count, count, count) // -> resources.getQuantityStringT(...)
```

Compose, both `androidx.compose.ui.res` and `org.jetbrains.compose.resources`:

```kotlin
stringResource(R.string.welcome_message)                 // -> com.anylocale.stringResource(...)
pluralStringResource(R.plurals.item_count, count, count) // -> com.anylocale.pluralStringResource(...)
```

Only Kotlin call sites are rewritten. Java code and calls made inside the Android framework
(for example `android:text` in a layout) are covered by `AnylocaleContextWrapper` instead.
