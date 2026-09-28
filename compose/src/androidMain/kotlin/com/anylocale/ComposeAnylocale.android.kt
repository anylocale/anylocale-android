package com.anylocale

import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.anylocale.model.AnylocaleMessageParams
import kotlinx.coroutines.flow.flowOf

/**
 * Retrieves a localized string resource based on the provided resource ID using Anylocale for translations.
 * If a translation is available from Anylocale, it is used; otherwise, the default string resource is returned.
 *
 * @param anylocale An instance of the Anylocale translation library for managing translations.
 * @param id The resource ID of the string to be translated or retrieved.
 * @return The translated or default string resource as a String.
 */
@Composable
fun stringResource(anylocale: Anylocale, @StringRes id: Int): String {
    val context = LocalContext.current
    val key = remember(id) {
        AnylocaleAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (anylocale as? AnylocaleAndroid)?.tFlow(context, id)
        ?: (key ?: AnylocaleAndroid.getKeyFromResources(context, id))?.let {
            anylocale.tFlow(key = it)
        } ?: flowOf(androidx.compose.ui.res.stringResource(id))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id)
    ).value
}

/**
 * Returns a localized string from the given resource ID.
 *
 * This function is a wrapper around the stringResource that integrates with
 * the Anylocale instance for localization. If Anylocale is not initialized, it falls
 * back to the standard Compose `stringResource`.
 *
 * @param id The resource ID of the string to retrieve.
 * @return The localized string associated with the provided resource ID.
 */
@Composable
fun stringResource(@StringRes id: Int): String {
    val instance = Anylocale.instanceOrNull ?: return androidx.compose.ui.res.stringResource(id)

    return stringResource(instance, id)
}

/**
 * Retrieves a string resource based on the given parameters, supporting dynamic translation
 * with Anylocale integration.
 *
 * @param anylocale The Anylocale instance used for translations.
 * @param id The resource ID of the string to be retrieved.
 * @param formatArgs Optional format arguments to replace placeholders in the string.
 * @return The translated or resource string corresponding to the specified resource ID.
 */
@Composable
fun stringResource(anylocale: Anylocale, @StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        AnylocaleAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (anylocale as? AnylocaleAndroid)?.tFlow(context, id, *formatArgs)
        ?: (key ?: AnylocaleAndroid.getKeyFromResources(context, id))?.let {
            anylocale.tFlow(key = it, parameters = AnylocaleMessageParams.Indexed(*formatArgs))
        } ?: flowOf(androidx.compose.ui.res.stringResource(id, *formatArgs))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringResource(id, *formatArgs)
    ).value
}

/**
 * Retrieves a localized string resource. If a `Anylocale` instance is available, it uses it to fetch
 * the translated string; otherwise, falls back to the default Compose string resource.
 *
 * @param id The resource ID of the string.
 * @param formatArgs The arguments to be used for formatting the string resource.
 * @return The localized string based on the provided resource ID and arguments.
 */
@Composable
fun stringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val instance = Anylocale.instanceOrNull ?: return androidx.compose.ui.res.stringResource(id, *formatArgs)

    return stringResource(instance, id, *formatArgs)
}

@Composable
fun pluralStringResource(anylocale: Anylocale, @PluralsRes id: Int, quantity: Int): String {
    val context = LocalContext.current
    val key = remember(id) {
        AnylocaleAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (anylocale as? AnylocaleAndroid)?.tPluralFlow(context.resources, id, quantity)
        ?: (key ?: AnylocaleAndroid.getKeyFromResources(context, id))?.let {
            anylocale.tFlow(key = it, parameters = AnylocaleMessageParams.Indexed(quantity))
        } ?: flowOf(androidx.compose.ui.res.pluralStringResource(id, quantity, quantity))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.pluralStringResource(id, quantity, quantity)
    ).value
}

@Composable
fun pluralStringResource(@PluralsRes id: Int, quantity: Int): String {
    val instance = Anylocale.instanceOrNull ?: return androidx.compose.ui.res.pluralStringResource(id, quantity, quantity)

    return pluralStringResource(instance, id, quantity)
}

@Composable
fun pluralStringResource(anylocale: Anylocale, @PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val key = remember(id) {
        AnylocaleAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (anylocale as? AnylocaleAndroid)?.tPluralFlow(context.resources, id, quantity, *formatArgs)
        ?: (key ?: AnylocaleAndroid.getKeyFromResources(context, id))?.let {
            anylocale.tFlow(key = it, parameters = AnylocaleMessageParams.Indexed(quantity, *formatArgs))
        } ?: flowOf(androidx.compose.ui.res.pluralStringResource(id, quantity, quantity, *formatArgs))

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.pluralStringResource(id, quantity, quantity, *formatArgs)
    ).value
}

@Composable
fun pluralStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val instance = Anylocale.instanceOrNull ?: return androidx.compose.ui.res.pluralStringResource(id, quantity, quantity, *formatArgs)

    return pluralStringResource(instance, id, quantity, *formatArgs)
}

@Composable
fun stringArrayResource(anylocale: Anylocale, @ArrayRes id: Int): Array<String> {
    val context = LocalContext.current
    val key = remember(id) {
        AnylocaleAndroid.getKeyFromResources(context, id)
    }

    val translationFlow = (anylocale as? AnylocaleAndroid)?.tArrayFlow(context.resources, id)
        ?: (key ?: AnylocaleAndroid.getKeyFromResources(context, id))?.let {
            anylocale.tArrayFlow(key = it)
        } ?: flowOf(androidx.compose.ui.res.stringArrayResource(id).toList())

    return translationFlow.collectAsState(
        initial = androidx.compose.ui.res.stringArrayResource(id).toList()
    ).value.toTypedArray()
}

@Composable
fun stringArrayResource(@ArrayRes id: Int): Array<String> {
    val instance = Anylocale.instanceOrNull ?: return androidx.compose.ui.res.stringArrayResource(id)
    return stringArrayResource(instance, id)
}