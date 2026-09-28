package com.anylocale.common

import com.anylocale.Anylocale
import io.ktor.client.*
import com.anylocale.storage.AnylocaleStorageProvider
import kotlin.coroutines.CoroutineContext

/**
 * Creates a platform-specific instance of the Anylocale localization service.
 *
 * This function is expected to be implemented differently for each platform to initialize a
 * platform-compatible Anylocale instance based on the provided configuration.
 *
 * @param config The configuration object for initializing the Anylocale instance, containing API details,
 *               project-specific settings, and other customization options.
 * @return A platform-specific instance of the Anylocale class, initialized with the given configuration.
 */
internal expect fun createPlatformAnylocale(config: Anylocale.Config): PlatformAnylocale

/**
 * Platform-specific instance of [HttpClient] used for making HTTP requests.
 * This property is expected to be provided by each platform's implementation
 * to enable network communication in a consistent manner.
 */
internal expect val platformHttpClient: HttpClient

/**
 * Expected declaration for the platform-specific CoroutineContext used for network-related operations.
 *
 * This property provides a platform-dependent coroutine context to manage concurrency in network interactions.
 * It is typically used as the default context for executing coroutines in networking scenarios.
 */
internal expect val platformNetworkContext: CoroutineContext

/**
 * Expected declaration for the platform-specific storage implementation.
 *
 * This property is expected to be provided by each platform's implementation
 * to enable storage-related functionalities.
 */
internal expect val platformStorage: AnylocaleStorageProvider?

/**
 * Platform-specific implementation of the Anylocale interface.
 * This class is expected to provide platform-dependent functionalities
 * for the Anylocale library, enabling internationalization and
 * localization features.
 */
expect class PlatformAnylocale : Anylocale