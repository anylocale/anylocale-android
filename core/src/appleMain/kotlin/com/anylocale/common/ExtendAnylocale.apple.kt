package com.anylocale.common

import de.comahe.i18n4k.createLocale
import com.anylocale.Anylocale
import com.anylocale.AnylocaleApple
import io.ktor.client.*
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.cache.*
import com.anylocale.storage.AnylocaleStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.Foundation.scriptCode
import platform.Foundation.variantCode
import kotlin.coroutines.CoroutineContext

/**
 * The default platform-specific HTTP client configured for Darwin-based platforms (e.g., iOS, macOS).
 *
 * This client includes the following configurations:
 * - Redirection following (`followRedirects = true`) to handle HTTP redirects automatically.
 * - Caching support through the HTTP cache plugin (`install(HttpCache)`).
 *
 * It serves as the default HTTP client for operations requiring network interactions when targeting Darwin platforms.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Darwin) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Creates a platform-specific instance of the Anylocale localization tool.
 *
 * @param config Configuration object used to initialize the Anylocale instance.
 * @return A platform-specific Anylocale instance.
 */
internal actual fun createPlatformAnylocale(config: Anylocale.Config): PlatformAnylocale {
    return AnylocaleApple(config)
}

/**
 * Provides the platform-specific CoroutineContext for performing network-related operations.
 *
 * This context ensures that network tasks are executed on an appropriate thread
 * or dispatcher, depending on the platform implementation.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

internal actual val platformStorage: AnylocaleStorageProvider?
    get() = null

/**
 * Provides a platform-specific type alias for the Anylocale localization framework implementation.
 * On Apple platforms, `PlatformAnylocale` is resolved to `AnylocaleApple`, which handles localization
 * functionality specific to Apple environments.
 */
actual typealias PlatformAnylocale = AnylocaleApple

/**
 * Sets the locale configuration for the builder and returns the instance for further customization.
 *
 * @param nsLocale The locale to be set for the builder.
 */
fun Anylocale.Config.Builder.locale(nsLocale: NSLocale) = locale(
    createLocale(
        language = nsLocale.languageCode,
        script = nsLocale.scriptCode?.ifBlank { null },
        country = nsLocale.countryCode?.ifBlank { null },
        variant = nsLocale.variantCode?.ifBlank { null }
    )
)