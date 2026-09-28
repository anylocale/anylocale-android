package com.anylocale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.intl.Locale
import com.anylocale.model.AnylocaleMessageParams
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource

/**
 * Retrieves a localized string resource using the provided Anylocale translation library and resource key.
 *
 * @param anylocale The Anylocale instance used for fetching translations.
 * @param resource The StringResource containing the key for the desired translation.
 * @return The localized string corresponding to the provided key.
 */
@Composable
fun stringResource(anylocale: Anylocale, resource: StringResource): String {
    return anylocale.tFlow(
        key = resource.key,
        parameters = AnylocaleMessageParams.None
    ).collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource)
    ).value
}

/**
 * Returns a localized string for the given string resource.
 *
 * This function checks if an instance of Anylocale exists. If it does, it retrieves the string
 * translation using Anylocale. Otherwise, it falls back to the default string resource resolution.
 *
 * @param resource The string resource to be localized.
 * @return The localized string corresponding to the given resource.
 */
@Composable
fun stringResource(resource: StringResource): String {
    val anylocale = Anylocale.instanceOrNull ?: return org.jetbrains.compose.resources.stringResource(resource)
    return stringResource(anylocale, resource)
}

/**
 * Composable function to retrieve a localized string resource using Anylocale.
 *
 * @param anylocale An instance of Anylocale used for fetching translations.
 * @param resource The resource key representing the string to be translated.
 * @param formatArgs Optional arguments to format the translated string.
 * @return The localized string resource.
 */
@Composable
fun stringResource(anylocale: Anylocale, resource: StringResource, vararg formatArgs: Any): String {
    return anylocale.tFlow(
        key = resource.key,
        parameters = AnylocaleMessageParams.Indexed(*formatArgs)
    ).collectAsState(
        initial = org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    ).value
}

/**
 * Provides a localized string for the given resource and format arguments.
 *
 * This function first attempts to use the Anylocale instance for retrieving the localized string.
 * If Anylocale is not initialized, it falls back to the Compose resource string method.
 *
 * @param resource The string resource containing the key for localization.
 * @param formatArgs Optional arguments to format the localized string.
 * @return The localized string formatted with the provided arguments.
 */
@Composable
fun stringResource(resource: StringResource, vararg formatArgs: Any): String {
    val anylocale = Anylocale.instanceOrNull ?: return org.jetbrains.compose.resources.stringResource(resource, *formatArgs)
    return stringResource(anylocale, resource, *formatArgs)
}

@Composable
fun pluralStringResource(anylocale: Anylocale, resource: PluralStringResource, quantity: Int): String {
    return anylocale.tFlow(
        key = resource.key,
        parameters = AnylocaleMessageParams.Indexed(quantity)
    ).collectAsState(
        initial = org.jetbrains.compose.resources.pluralStringResource(resource, quantity)
    ).value
}

@Composable
fun pluralStringResource(resource: PluralStringResource, quantity: Int): String {
    val anylocale = Anylocale.instanceOrNull ?: return org.jetbrains.compose.resources.pluralStringResource(resource, quantity)
    return pluralStringResource(anylocale, resource, quantity)
}

@Composable
fun pluralStringResource(anylocale: Anylocale, resource: PluralStringResource, quantity: Int, vararg formatArgs: Any): String {
    return anylocale.tFlow(
        key = resource.key,
        parameters = AnylocaleMessageParams.Indexed(quantity, *formatArgs)
    ).collectAsState(
        initial = org.jetbrains.compose.resources.pluralStringResource(resource, quantity, quantity, *formatArgs)
    ).value
}

@Composable
fun pluralStringResource(resource: PluralStringResource, quantity: Int, vararg formatArgs: Any): String {
    val anylocale = Anylocale.instanceOrNull ?: return org.jetbrains.compose.resources.pluralStringResource(resource, quantity, quantity, *formatArgs)
    return pluralStringResource(anylocale, resource, quantity, *formatArgs)
}

@Composable
fun stringArrayResource(anylocale: Anylocale, resource: StringArrayResource): List<String> {
    return anylocale.tArrayFlow(
        key = resource.key,
    ).collectAsState(
        initial = org.jetbrains.compose.resources.stringArrayResource(resource)
    ).value
}

@Composable
fun stringArrayResource(resource: StringArrayResource): List<String> {
    val anylocale = Anylocale.instanceOrNull ?: return org.jetbrains.compose.resources.stringArrayResource(resource)
    return stringArrayResource(anylocale, resource)
}

/**
 * Sets the locale configuration for the builder and returns the instance for further customization.
 *
 * @param composeLocale The locale to be set for the builder.
 */
fun Anylocale.Config.Builder.locale(composeLocale: Locale) = locale(composeLocale.toLanguageTag())

/**
 * Sets the current locale for the Anylocale instance, updating it in the reactive locale flow.
 *
 * @param composeLocale The locale to be set for translations and related operations.
 */
fun Anylocale.setLocale(composeLocale: Locale) = setLocale(composeLocale.toLanguageTag())