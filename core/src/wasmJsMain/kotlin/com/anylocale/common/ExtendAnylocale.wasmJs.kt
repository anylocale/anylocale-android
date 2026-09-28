package com.anylocale.common

import com.anylocale.Anylocale
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.cache.*
import com.anylocale.storage.AnylocaleStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Creates and returns a platform-specific instance of Anylocale.
 *
 * @param config The configuration object required to initialize the Anylocale instance.
 * @return A newly created instance of Anylocale initialized with the provided configuration.
 */
internal actual fun createPlatformAnylocale(config: Anylocale.Config): PlatformAnylocale {
    return PlatformAnylocale(config)
}

/**
 * A platform-specific instance of [HttpClient] configured for JavaScript environments.
 *
 * - Uses the `Js` engine to interact with network resources in JavaScript runtime.
 * - Enables automatic following of HTTP redirects through `followRedirects = true`.
 * - Installs an HTTP cache plugin (`HttpCache`) for caching network responses when appropriate.
 *
 * This client is typically utilized as the default network client across the platform-specific
 * network infrastructure in the application.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Js) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Represents the coroutine context used for network-related operations on the current platform.
 *
 * This context is typically used by default in platform-specific implementations
 * to provide a standardized coroutine execution environment for network tasks.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.Default

internal actual val platformStorage: AnylocaleStorageProvider?
    get() = null

/**
 * A platform-specific implementation of the Anylocale class.
 * This class extends the Anylocale base class and provides platform-dependent behavior.
 *
 * @constructor Creates an instance of PlatformAnylocale with the specified configuration.
 * @param config The configuration object used to initialize the Anylocale instance.
 */
@ConsistentCopyVisibility
actual data class PlatformAnylocale internal constructor(override val config: Config) : Anylocale(config)