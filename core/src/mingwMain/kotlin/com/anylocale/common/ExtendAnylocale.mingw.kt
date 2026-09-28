package com.anylocale.common

import com.anylocale.Anylocale
import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.cache.*
import com.anylocale.storage.AnylocaleStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of the Anylocale class with the given configuration.
 *
 * @param config The configuration object used to initialize the Anylocale instance.
 * @return A platform-specific instance of the Anylocale class.
 */
internal actual fun createPlatformAnylocale(config: Anylocale.Config): PlatformAnylocale {
    return PlatformAnylocale(config)
}

/**
 * Provides a platform-specific instance of the `HttpClient` configured with the WinHttp engine.
 *
 * This `HttpClient` is set up with the following configurations:
 * - Redirects are automatically followed (`followRedirects = true`).
 * - The HTTP cache plugin is installed for caching support.
 *
 * This client is utilized as the default HTTP client for network-related operations on
 * Windows platforms within the library.
 */
internal actual val platformHttpClient: HttpClient = HttpClient(WinHttp) {
    followRedirects = true
    install(HttpCache)
}

/**
 * Provides a platform-specific coroutine context for network operations.
 *
 * Represents the default coroutine context used for executing network-related tasks on the current platform.
 * Typically, this context is based on the `Dispatchers.IO` dispatcher, optimized for IO-bound operations.
 */
internal actual val platformNetworkContext: CoroutineContext
    get() = Dispatchers.IO

internal actual val platformStorage: AnylocaleStorageProvider?
    get() = null

/**
 * Actual implementation of the `PlatformAnylocale` class for a specific platform.
 * It extends the `Anylocale` base class and is initialized with a `Config` object.
 *
 * This class is used to provide platform-specific implementations or configurations
 * for the Anylocale library.
 *
 * @constructor Creates a `PlatformAnylocale` instance with the provided configuration.
 * @param config The configuration object used to initialize the `PlatformAnylocale` instance.
 */
@ConsistentCopyVisibility
actual data class PlatformAnylocale internal constructor(override val config: Config) : Anylocale(config)