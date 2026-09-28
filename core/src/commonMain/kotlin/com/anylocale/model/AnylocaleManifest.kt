package com.anylocale.model

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import kotlinx.serialization.Serializable

/**
 * Represents metadata about available translations in the project.
 *
 * This metadata is fetched from the CDN and contains information about
 * which locales are available for translation fallback logic.
 *
 * See `Anylocale.resolveLocale` for the fallback order.
 *
 * @property locales List of locale tags (e.g., ["en", "en-US", "zh", "zh-Hans", "zh-Hans-CN"])
 *                   that are available in this Anylocale project. If null, the
 *                   fallback mechanism is disabled and only exact locale matches
 *                   will be used.
 */
@Serializable
internal data class AnylocaleManifest(
    val locales: List<String>?
) {
    /**
     * Converts the string locale tags to Locale objects.
     * Lazily computed and cached for performance.
     * Returns null if locales is null (fallback disabled).
     */
    val availableLocales: List<Locale>? by lazy {
        locales?.map { forLocaleTag(it) }
    }
}
