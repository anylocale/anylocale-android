package com.anylocale.common

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.cache.*
import com.anylocale.Anylocale
import com.anylocale.storage.AnylocaleStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of Anylocale using the provided configuration.
 *
 * @param config The configuration object to initialize the Anylocale instance.
 * @return A platform-specific instance of Anylocale.
 */
internal actual fun createPlatformAnylocale(config: Anylocale.Config): PlatformAnylocale {
    return PlatformAnylocale(config)
}

/**
 * Platform-specific instance of [HttpClient] configured for JavaScript environments.
 *
 * This HTTP client is preconfigured to:
 * - Follow redirects automatically.
 * - Utilize the HTTP cache for request optimizations.
 *
 * It uses the JavaScript engine (`Js`) as its backend and is initialized with
 * relevant plugins for typical use cases in browser or JavaScript-based applications.
 *
 * This client is marked as `internal` and primarily intended for internal usage
 * within the platform-specific implementations.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(Js) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Represents the platform-specific coroutine context for network operations.
 *
 * This property provides a `CoroutineContext` that is optimized for network-related tasks on the current platform.
 * The context is used for executing suspending functions and coroutine-based workflows that involve network interactions.
 *
 * On some platforms, this is typically based on `Dispatchers.Default` or equivalent, ensuring appropriate thread usage
 * and performance.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.Default

internal actual val platformStorage: AnylocaleStorageProvider?
    get() = null

/**
 * A platform-specific implementation of the `Anylocale` class.
 *
 * This class provides platform-specific configuration and behavior for Anylocale functionalities
 * and extends the core `Anylocale` class by implementing platform-targeted logic.
 *
 * @constructor Creates an instance of `PlatformAnylocale` with the provided configuration.
 * @param config The configuration used to initialize the Anylocale instance.
 */
@ConsistentCopyVisibility
actual data class PlatformAnylocale internal constructor(override val config: Config) : Anylocale(config)