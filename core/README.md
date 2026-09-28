# anylocale SDK: core module

`com.anylocale:sdk`. The Kotlin Multiplatform runtime: fetches a distribution from
anylocale, caches it, resolves locales, formats strings, and on Android plugs into
`Context`, `Resources` and layout inflation. The Compose module builds on top of it.

The [top-level README](../README.md) covers installation, initialisation, the wire format,
locale resolution, Views usage, plurals and the version-code cache. This file lists what is
specific to this module.

## Setup

```toml
[libraries]
anylocale = { group = "com.anylocale", name = "sdk", version.ref = "anylocale" }
```

```kotlin
dependencies {
    implementation(libs.anylocale)
}
```

If your app ships a restrictive `network_security_config`, allow `anylocale.com`:

```xml
<network-security-config xmlns:android="http://schemas.android.com/apk/res/android">
    <domain-config>
        <domain includeSubdomains="true">anylocale.com</domain>
    </domain-config>
</network-security-config>
```

## Initialisation

```kotlin
Anylocale.init {
    contentDelivery {
        url = "https://anylocale.com/ota/v1/<distribution-key>"
        storage = AnylocaleStorageProviderAndroid(this@MyApplication, BuildConfig.VERSION_CODE)
        formatter(Anylocale.Formatter.Sprintf)     // default; Anylocale.Formatter.ICU is the alternative
        availableLocaleTags("en", "cs", "fr")      // optional, skips the manifest request
        maxLocalesInMemory(1)                      // optional, LRU size; null for unlimited
    }
    defaultLanguage("en")                          // optional, final fallback locale
    locale("en")                                   // optional, initial locale; defaults to the system locale
}
```

Non-Android platforms have no built-in storage; implement `AnylocaleStorageProvider`
(`put(name, bytes)` / `get(name)`) or leave it out to run without persistence.

## Platform-independent API

```kotlin
val anylocale = Anylocale.instance          // Anylocale.instanceOrNull before init

anylocale.t("key")                           // String?, null until translations are loaded
anylocale.t("key", AnylocaleMessageParams.Indexed(1, "x"))
anylocale.t("key", AnylocaleMessageParams.Mapped(mapOf("name" to "x")))   // ICU only
anylocale.tArray("key")                      // List<String>
anylocale.tFlow("key")                       // Flow<String>, re-emits on locale or content change
anylocale.tArrayFlow("key")

anylocale.preload()                          // suspend: manifest plus current locale
anylocale.preloadAll()                       // suspend: every available locale

anylocale.setLocale("cs")
anylocale.getLocale()
anylocale.availableLocaleTags                // from config or manifest; null before preload
anylocale.changeFlow                         // Flow<Unit>
anylocale.addChangeListener { }              // Anylocale.ChangeListener, for Java callers
```

`Anylocale.new { }` builds an instance without registering it as the singleton.

## Android

`Anylocale.instance` is an `AnylocaleAndroid` on Android and adds resource-id based lookups
(`t(context, R.string.x)`, `tPlural`, `tArray`, `tStyled`, `tFlow(context, id)`),
`preload(lifecycleOwner)`, `retranslate(activity)` and `retranslate(view)`.

`AnylocaleContextWrapper.wrap(base)` in `attachBaseContext` intercepts `getString`/`getText`
and their plural and array variants, and installs a `LayoutInflater.Factory2` that translates
`android:text`, `android:hint` and `android:contentDescription` during inflation. The
constructor flags `interceptGetString`, `interceptGetText` and `argumentLayoutInflater`
switch each part off.

The translation key of a resource is its entry name (`resources.getResourceEntryName`), so
`R.string.welcome_message` looks up `welcome_message` in the distribution.

## Apple

`Anylocale.instance` is an `AnylocaleApple`, which adds `t(key, default, table)` and
`tFlow(key, default, table)` with fallback to `NSBundle` localizations, and
`setLocale(NSLocale)`. The framework is published as `KMPAnylocale`.

## Troubleshooting

- `t()` returns null: translations are not loaded yet. Call `preload` or collect `tFlow`
  first.
- Nothing updates: check the distribution URL, that `Anylocale.init` ran before the first
  lookup, and that the locale exists in the distribution (`availableLocaleTags`).
- Views not retranslating: only attributes that reference a string resource without format
  arguments are handled during inflation; set the rest in `changeFlow`.
