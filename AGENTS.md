# AGENTS.md

Guidance for AI coding agents working on the anylocale Android / Kotlin Multiplatform SDK.

Main branch: `master`

## Project Structure

```
anylocale-android/
├── core/              # com.anylocale:sdk, KMP base library (translations, caching, content delivery)
├── compose/           # com.anylocale:sdk-compose, Compose Multiplatform integration
├── compiler-plugin/   # com.anylocale:sdk-compiler-plugin, Kotlin compiler plugin (currently disabled)
├── gradle-plugin/     # com.anylocale:sdk-gradle-plugin, plugin id com.anylocale.sdk
└── demo/              # Example apps (Android Views, Jetpack Compose, KMP Compose)
```

The compiler plugin is commented out in `settings.gradle.kts` (broken after Kotlin 2.2).

Maven group is `com.anylocale`; Kotlin packages are `com.anylocale.*`; public types are
`Anylocale`, `AnylocaleAndroid`, `AnylocaleApple`, `AnylocaleContextWrapper`,
`AnylocaleStorageProvider`, `AnylocaleStorageProviderAndroid`, `AnylocaleMessageParams`.

## Wire format (do not change)

The server contract is fixed: `<url>/manifest.json` (`{"locales": [...]}`) and
`<url>/<locale>.json` (flat object; values are a string, a plural object keyed by CLDR
category, or a string array), decoded in `core/.../api/AnylocaleApi.kt` and
`common/ExtendParser.kt`. Sprintf and ICU formatting, the `sdkType`/`sdkVersion` headers and
Ktor `HttpCache` (ETag) handling stay as they are.

## Building

Gradle 8.14.3 runs on JDK 17 to 24; `core` and `compose` declare `jvmToolchain(21)`, so a
JDK 21 must be discoverable (CI installs Temurin 21). Android builds need
`ANDROID_HOME` or `local.properties`.

```bash
./gradlew build                # everything CI builds, including Apple/native targets on macOS
./gradlew :core:build
./gradlew :compose:build
./gradlew :gradle-plugin:build
./gradlew :demo:exampleandroid:assembleDebug :demo:examplejetpack:assembleDebug
```

## Testing

```bash
./gradlew :core:jvmTest                  # commonTest (locale resolution) on the JVM
./gradlew :core:testDebugUnitTest        # the same tests on the Android target
./gradlew :gradle-plugin:test
./gradlew :gradle-plugin:test --tests "AnylocaleTest"
```

CI (`test.yml`) runs `./gradlew build` on ubuntu and macos for every push and pull request.

## API Compatibility

The project uses the Binary Compatibility Validator plugin. After any change to public APIs, run:

```bash
./gradlew apiDump
```

This updates the `.api` dump files in each module's `api/` directory. These files **must be
committed** with the change. The build fails (`apiCheck`) if a dump is out of date.

## Module Architecture

### Core Module

Main entry point: `Anylocale` singleton class in `core/src/commonMain/kotlin/com/anylocale/Anylocale.kt`

Key components:
- `Anylocale`: singleton with locale management, translation resolution, and configuration
- `AnylocaleApi`: content delivery communication and local caching
- `AnylocaleTranslation`: interface with ICU and sprintf formatting implementations
- `AnylocaleStorageProvider`: platform-specific persistent caching interface
- `AnylocaleManifest`: available locales metadata from the distribution (internal)

Data flow:
```
tFlow("key", params)
  -> localeFlow emits locale
  -> loadManifest() fetches available locales
  -> resolveLocale() applies progressive BCP 47 fallback (zh-Hans-CN -> zh-Hans -> zh-CN -> zh)
  -> loadTranslations() fetches from the network or cache (LRU in-memory + persistent storage)
  -> translation formatted and emitted via Flow
```

Thread safety: `Mutex` for translation loading, `AtomicFU` for the manifest cache.

### Compose Module

Composable wrappers around Core with graceful fallback to default Compose resources when
`Anylocale` is not initialised. Provides `stringResource()`, `pluralStringResource()`,
`stringArrayResource()` in package `com.anylocale`.

### Gradle Plugin

Build-time configuration DSL (`anylocale { compilerPlugin { android { } compose { } } }`)
bridging Gradle with the compiler plugin.

### Compiler Plugin (disabled)

Kotlin IR transformations that replace standard resource calls with the anylocale
equivalents at compile time. Plugin id `com.anylocale.sdk.compiler-plugin`, option keys
`anylocale.android.getString`, `anylocale.android.pluralString`,
`anylocale.compose.stringResource`, `anylocale.compose.pluralStringResource`.

## Multiplatform

The core module targets 20+ platforms:

| Category | Targets |
|----------|---------|
| Android | androidTarget, androidNativeX64/X86/Arm64/Arm32 |
| JVM | jvm (toolchain 21) |
| Apple | iOS (x64, arm64, simulatorArm64), tvOS (x3), watchOS (x4), macOS (x64, arm64) |
| Other | linuxX64, linuxArm64, mingwX64, js (IR), wasmJs |

Source set hierarchy follows `applyDefaultHierarchyTemplate()`:
- `commonMain`: all shared code
- `androidMain`: Android-specific (Views integration, storage)
- `appleMain`: Apple platforms (iOS/macOS/tvOS/watchOS)
- `jvmMain`: JVM-specific
- `jsMain` / `wasmJsMain`: JS/WASM

## Key Design Patterns

- **Builder pattern** for configuration: `Anylocale.Config.Builder`, `ContentDelivery.Builder`, `Network.Builder`
- **Sealed interfaces** for type safety: `Formatter` (ICU/Sprintf), `AnylocaleMessageParams` (None/Indexed/Mapped)
- **Reactive Flows**: `localeFlow`, `changeFlow`, `tFlow()` returning `Flow<String>`
- **Expect/actual** for platform-specific implementations (`platformHttpClient`, `platformStorage`, `PlatformAnylocale`)

## Dependencies

Managed via the version catalog in `gradle/libs.versions.toml`. Add new dependencies there,
not directly in build scripts.

Key libraries: Ktor (HTTP), kotlinx-serialization (JSON), kotlinx-coroutines (async),
i18n4k (locale handling), AtomicFU (thread safety), SKIE (Swift interop for Apple targets).

## Conventions

- Conventional commits (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`), hyphens only, no
  em dashes in code or commit messages.
- Do not mention the SDK this project started from anywhere except `NOTICE` and the
  attribution line at the top of `README.md`.

## Publishing

Manual trigger via the `publish.yml` workflow on macOS. Publishes to Maven Central via
`publishAllPublicationsToMavenCentralRepository`. Requires signing keys and Sonatype
credentials. `apple-binaries.yml` attaches the `KMPAnylocale` XCFrameworks to a GitHub
release.
