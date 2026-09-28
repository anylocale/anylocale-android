package com.anylocale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.anylocale.model.AnylocaleMessageParams

/**
 * Provides a localized string based on the given key and optional default, using the specified Anylocale instance.
 *
 * @param anylocale An instance of the Anylocale library used for retrieving translations.
 * @param key The key used to look up the localized string.
 * @param default The default string returned if no translation is found. Can be null.
 * @param table An optional parameter specifying the translation table or namespace. Defaults to null.
 * @return The localized string corresponding to the provided key, or a fallback based on the default and table parameters.
 */
@Composable
fun stringResource(anylocale: Anylocale, key: String, default: String?, table: String? = null): String {
    val translationFlow = (anylocale as? AnylocaleApple)?.tFlow(key, default, table) ?: anylocale.tFlow(key)
    val res = anylocale.getLocale().getLanguage().ifBlank { null }

    return translationFlow.collectAsState(
        initial = AnylocaleApple.getLocalizedStringFromBundle(res, key, default, table) ?: default?.ifBlank { null } ?: ""
    ).value
}

/**
 * Retrieves a localized string based on the provided key, default value, and optional table name.
 *
 * If a localization instance is available, it fetches the translation using it.
 * Otherwise, it attempts to fetch the localized string from the application bundle or returns the default string.
 *
 * @param key The key used to identify the localized string.
 * @param default The default string to use if no localized string is found. If blank, it will be treated as null.
 * @param table The optional table or namespace for looking up the key. Defaults to null if not specified.
 * @return The localized string if available, or the fallback string based on the provided default or key.
 */
@Composable
fun stringResource(key: String, default: String?, table: String? = null): String {
    val instance = Anylocale.instanceOrNull ?: return run {
        val res = Anylocale.systemLocale.getLanguage().ifBlank { null }

        AnylocaleApple.getLocalizedStringFromBundle(res, key, default, table) ?: default?.ifBlank { null } ?: ""
    }

    return stringResource(instance, key, default, table)
}

/**
 * Retrieves a localized string resource using the provided translation key, default value, optional table,
 * and optional arguments. The translation is fetched from the specified Anylocale instance and updates
 * dynamically using a state-based approach.
 *
 * @param anylocale The Anylocale instance responsible for fetching translations.
 * @param key The translation key used to identify the desired string resource.
 * @param default The default string to use if no translation is found.
 * @param table The optional table name where the key is located.
 * @param args Optional arguments for formatting the translated string.
 * @return A string value representing the localized and formatted resource.
 */
@Composable
fun stringResource(anylocale: Anylocale, key: String, default: String?, table: String? = null, vararg args: Any): String {
    val translationFlow = (anylocale as? AnylocaleApple)?.tFlow(key, default, table, *args)
        ?: anylocale.tFlow(key, AnylocaleMessageParams.Indexed(*args))
    val res = anylocale.getLocale().getLanguage().ifBlank { null }

    return translationFlow.collectAsState(
        initial = AnylocaleApple.getLocalizedStringFromBundleFormatted(res, key, default, table, *args) ?: ""
    ).value
}

/**
 * Retrieves a localized and formatted string based on the specified key, default value, table, and arguments.
 * The method ensures the use of a `Anylocale` instance if available, otherwise falls back to bundle-based localization.
 *
 * @param key The key identifying the localized string resource.
 * @param default The default string to use if no localization is found. Can be null.
 * @param table The optional table from which the localization data is fetched. Can be null.
 * @param args The arguments to format the localized string.
 * @return The localized and formatted string; returns an empty string if no localization is found and no default value is provided.
 */
@Composable
fun stringResource(key: String, default: String?, table: String? = null, vararg args: Any): String {
    val instance = Anylocale.instanceOrNull ?: return run {
        val res = Anylocale.systemLocale.getLanguage().ifBlank { null }

        AnylocaleApple.getLocalizedStringFromBundleFormatted(res, key, default, table, *args) ?: ""
    }

    return stringResource(instance, key, default, table, *args)
}