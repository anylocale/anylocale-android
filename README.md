# anylocale SDK for Android and Compose Multiplatform

Over-the-air string updates for Android and Kotlin Multiplatform apps. The SDK downloads
the translations you publish on [anylocale](https://anylocale.com) at runtime, caches them
on the device, and serves them through the string APIs you already use: Android resources,
`Context.getString`, Jetpack Compose `stringResource`, Compose Multiplatform `Res.string`.
Bundled resources stay the fallback, so nothing breaks offline or before the first fetch.

## Modules

| Coordinates                           | Directory         | Use it for                                                                |
| ------------------------------------- | ----------------- | ------------------------------------------------------------------------- |
| `com.anylocale:sdk`                   | `core/`           | Android Views, plain Kotlin, Kotlin Multiplatform                          |
| `com.anylocale:sdk-compose`           | `compose/`        | Jetpack Compose and Compose Multiplatform (depends on `sdk`)               |
| `com.anylocale.sdk` (Gradle plugin)   | `gradle-plugin/`  | The `anylocale { }` build DSL; the compile-time rewriting is currently off |
| `com.anylocale:sdk-compiler-plugin`   | `compiler-plugin/`| Kotlin IR rewriting of `getString`/`stringResource`; not built right now  |

`core` targets Android, JVM, iOS/tvOS/watchOS/macOS, Linux, Windows, JS and Wasm.
`compose` targets Android, JVM, iOS, macOS, JS and Wasm. Android `minSdk` is 21.

## Installation

```toml
# gradle/libs.versions.toml
[versions]
anylocale = "1.0.0-alpha05"

[libraries]
anylocale = { group = "com.anylocale", name = "sdk", version.ref = "anylocale" }
anylocale-compose = { group = "com.anylocale", name = "sdk-compose", version.ref = "anylocale" }
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.anylocale)          // Views
    implementation(libs.anylocale.compose)  // Compose, instead of or in addition to the line above
}
```

`sdk-compose` already pulls in `sdk`; add `sdk` on its own only for Views-only apps.

## Initialisation

Publish a distribution on anylocale and copy its key. Initialise once, before any string is
read, typically in `Application.onCreate`:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Anylocale.init {
            contentDelivery {
                url = "https://anylocale.com/ota/v1/<distribution-key>"
                storage = AnylocaleStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
            }
            defaultLanguage("en")
        }
    }
}
```

Java:

```java
Anylocale.Config.ContentDelivery contentDelivery = new Anylocale.Config.ContentDelivery.Builder()
        .url("https://anylocale.com/ota/v1/<distribution-key>")
        .storage(new AnylocaleStorageProviderAndroid(this, BuildConfig.VERSION_CODE))
        .build();

Anylocale.init(new Anylocale.Config.Builder()
        .contentDelivery(contentDelivery)
        .defaultLanguage("en")
        .build());
```

On other platforms leave `storage` out (or supply your own `AnylocaleStorageProvider`); the
SDK then keeps translations in memory only.

### What the SDK fetches

Given the distribution URL `<url>`, the SDK requests:

- `<url>/manifest.json`: `{"locales": ["en", "cs", "zh-Hans"]}`, the locales available in
  the distribution. Used for locale fallback. Skip it with `availableLocaleTags("en", "cs")`.
- `<url>/<locale>.json`: a flat object keyed by string name. A value is either a string, an
  object keyed by CLDR plural category (`{"one": "...", "other": "..."}`), or an array of
  strings (for `<string-array>`).

Requests carry `sdkType` and `sdkVersion` headers. The HTTP client honours `ETag` and
`Cache-Control`; the persistent storage is the offline fallback when the network fetch fails.

### Locale resolution

The requested locale is matched against the available locales case-insensitively, most
specific first, following BCP 47 structure:

```
zh-Hans-CN -> zh-Hans -> zh-CN -> zh -> en (defaultLanguage)
```

Without `defaultLanguage`, an unavailable locale falls back to the bundled resources.

### Formatting

Translations are formatted with Android `sprintf` semantics by default (`%1$s`, `%d`), which
matches what `strings.xml` already contains. Switch to ICU MessageFormat when your
distribution is published in that format:

```kotlin
Anylocale.init {
    contentDelivery {
        url = "https://anylocale.com/ota/v1/<distribution-key>"
        formatter(Anylocale.Formatter.ICU)
    }
}
```

## Android Views

Wrap the base context of every Activity. `getString`, `getText`, `getQuantityString`,
`getStringArray` and their `Resources` counterparts then return the downloaded translation
when one exists, and the bundled resource otherwise:

```kotlin
class MainActivity : ComponentActivity() {

    private val anylocale = Anylocale.instance

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(AnylocaleContextWrapper.wrap(newBase))
    }

    override fun onStart() {
        super.onStart()
        anylocale.preload(this)   // fetch in the background, emits changeFlow when done
    }
}
```

The wrapper also installs a `LayoutInflater.Factory2`, so `android:text`, `android:hint` and
`android:contentDescription` attributes that reference a string resource are translated during
inflation. Strings with format arguments are skipped there; set those yourself.

React to updates without recreating the Activity:

```kotlin
lifecycleScope.launch {
    anylocale.changeFlow.collect {
        anylocale.retranslate(this@MainActivity)          // re-applies inflated attributes
        title = getString(R.string.app_name)               // anything set in code, set again
    }
}
```

From Java, implement `Anylocale.ChangeListener` and register it with `addChangeListener`.

Direct lookups, all falling back to the bundled resource:

```kotlin
anylocale.t(context, R.string.title)
anylocale.t(context, R.string.greeting, name)
anylocale.tPlural(resources, R.plurals.items, count, count)
anylocale.tArray(resources, R.array.options)
anylocale.tFlow(context, R.string.title)   // Flow<String>, re-emits on locale or content change
```

Extension functions with the same behaviour exist on `Context` and `Resources`:
`getStringT`, `getQuantityStringT`, `getStringArrayT`, `getTextT`, `getQuantityTextT`,
`getTextArrayT`.

Switch language at runtime:

```kotlin
anylocale.setLocale("cs")          // or a java.util.Locale
anylocale.preload(this)
```

## Compose

Import the composables from `com.anylocale` instead of `androidx.compose.ui.res` or
`org.jetbrains.compose.resources`. The signatures match, the initial value is the bundled
resource, and the composable recomposes when the translation arrives or the locale changes.

Jetpack Compose:

```kotlin
import com.anylocale.pluralStringResource
import com.anylocale.stringResource

@Composable
fun Screen(name: String, count: Int) {
    Text(stringResource(R.string.description))
    Text(stringResource(R.string.greeting, name))
    Text(pluralStringResource(R.plurals.items, count, count))
}
```

Compose Multiplatform:

```kotlin
Text(stringResource(Res.string.description))
Text(stringResource(Res.string.greeting, name))
Text(pluralStringResource(Res.plurals.items, count, count))
Text(stringArrayResource(Res.array.options).joinToString())
```

Every composable has an overload taking an explicit instance as its first argument, for
apps that create one with `Anylocale.new { }` instead of the singleton:

```kotlin
Text(stringResource(anylocale, Res.string.description))
```

Observe the locale:

```kotlin
val locale = anylocale.changeFlow
    .mapLatest { anylocale.getLocale() }
    .collectAsState(initial = anylocale.getLocale())
```

## Plurals

Android `<plurals>` publish as a JSON object keyed by CLDR category:

```json
"items": { "one": "%1$d item", "other": "%1$d items" }
```

With the sprintf formatter the first indexed argument selects the category for the current
locale, so pass the quantity first exactly as `getQuantityString` expects:

```kotlin
resources.getQuantityString(R.plurals.items, count, count)
pluralStringResource(R.plurals.items, count, count)
```

With the ICU formatter the plural lives inside the message (`{0, plural, one {...} other {...}}`)
and the arguments are passed unchanged.

## Cache keyed by version code

`AnylocaleStorageProviderAndroid(context, versionCode)` writes each downloaded file to
`filesDir/anylocale/localization-cache/<versionCode>/`. Pass `BuildConfig.VERSION_CODE`: a
new app version starts with an empty cache, so translations downloaded for an older build,
whose keys or placeholders may no longer match, are never served to the new one. The
directory name is the third constructor parameter if you need a different location.

Reads go network first, storage second. When the fetch fails the last stored copy is used;
when it succeeds the stored copy is replaced.

In memory the SDK keeps one locale by default. Raise `maxLocalesInMemory(n)` in
`contentDelivery { }` for apps that switch languages often, or call `preloadAll()` to warm
every available locale.

## Building this repository

```bash
./gradlew build                       # what CI runs; needs JDK 21 and, for Apple targets, Xcode
./gradlew :core:jvmTest               # unit tests
./gradlew :gradle-plugin:test
./gradlew apiDump                     # after any public API change; commit the api/ files
./gradlew :demo:exampleandroid:assembleDebug :demo:examplejetpack:assembleDebug
./gradlew :demo:multiplatform-compose:run
```

Demo apps live in `demo/`: `exampleandroid` (Views, Kotlin and Java Activities),
`examplejetpack` (Jetpack Compose) and `multiplatform-compose` (Compose Multiplatform,
desktop). Replace the distribution URL in their `MyApplication`/`Setup` before running them
against your own content.

### Local server

`exampleandroid` and `examplejetpack` currently point at a local anylocale server,
`http://10.0.2.2:3005/ota/v1/<key>` (the emulator's address for the host's localhost), with
the key of a Kotlin distribution of the `localdev` project (locales de, en, es, fr). Debug
builds permit cleartext HTTP to `10.0.2.2` through `src/debug/res/xml/network_security.xml`;
release builds keep the `anylocale.com` only configuration.

Both demos show four keys that server publishes: `home.feels_like` (a layout `@string`
reference in the Views demo, `stringResource` in Compose), `home.hourly_forecast` and
`error.no_connection` (`getString`), and the plural `home.rain_alerts_count` with quantities 1
and 3 (`getQuantityString` / `pluralStringResource`). Resource names keep the dots, which aapt
turns into underscores in `R` (`R.string.home_feels_like`) while `getResourceEntryName` still
returns `home.feels_like`, the key the SDK sends to the server. The bundled values in
`res/values*/strings.xml` are prefixed `[local]`, so a string that still comes from the
bundle is visible at a glance.

## Release notes

### Unreleased

- Rebranded for anylocale: Maven group `com.anylocale`, artifacts `sdk` and `sdk-compose`,
  Gradle plugin id `com.anylocale.sdk`, Kotlin packages `com.anylocale.*`, public types
  `Anylocale`, `AnylocaleAndroid`, `AnylocaleApple`, `AnylocaleContextWrapper`,
  `AnylocaleStorageProvider`, `AnylocaleStorageProviderAndroid`, `AnylocaleMessageParams`.
- Apple framework renamed to `KMPAnylocale`.
- The Android storage provider now writes under `anylocale/localization-cache` instead of the
  previous directory name; existing on-device caches are not reused.
- Content delivery URL layout, JSON format, sprintf/ICU formatting and HTTP caching are
  unchanged from the SDK this project started from, so a distribution served at
  `<url>/<locale>.json` with `<url>/manifest.json` works as before.

## License

Apache License 2.0, see [LICENSE](LICENSE) and [NOTICE](NOTICE).
