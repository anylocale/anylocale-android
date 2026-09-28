package com.anylocale

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.LayoutInflater
import kotlinx.atomicfu.atomic

/**
 * A custom [ContextWrapper] that overrides string retrieval to fetch translations from the Anylocale platform.
 *
 * This class intercepts calls to [getString] and attempts to load the string from Anylocale.
 * If no translation is loaded or found, it falls back to the default [Context.getString] implementation.
 * Calls to [getText] are also intercepted, if the string is found in Anylocale. The returned value is not formatted.
 * (behaves like [Context.getText])
 *
 * Additionally, this wrapper installs a [AnylocaleLayoutInflaterFactory] to automatically translate
 * text attributes during layout inflation.
 *
 * @param base The base [Context] to wrap.
 * @param anylocale The Anylocale translation service used for retrieving localized strings.
 */
class AnylocaleContextWrapper(
    val base: Context,
    val anylocale: Anylocale,
    val interceptGetString: Boolean = true,
    val interceptGetText: Boolean = true,
    val argumentLayoutInflater: Boolean = true
) : ContextWrapper(base) {

    private var baseRes by atomic<Resources?>(null)
    private var res by atomic<Resources?>(baseRes)
    private var layoutInflater by atomic<LayoutInflater?>(null)

    override fun getResources(): Resources? {
        val base = super.getResources() ?: return res

        if ((res == null || baseRes != base) && (interceptGetString || interceptGetText)) {
            res = AnylocaleResources(base, anylocale, interceptGetString, interceptGetText)
            baseRes = base
        }

        return res ?: base
    }

    override fun getSystemService(name: String): Any? {
        if (LAYOUT_INFLATER_SERVICE == name) {
            if (layoutInflater == null && anylocale is AnylocaleAndroid && argumentLayoutInflater) {
                val baseInflater = super.getSystemService(name) as? LayoutInflater
                baseInflater?.let {
                    val cloned = it.cloneInContext(this)
                    installFactory2(cloned, anylocale)
                    layoutInflater = cloned
                }
            }
            return layoutInflater ?: super.getSystemService(name)
        }
        return super.getSystemService(name)
    }

    /**
     * Installs the [AnylocaleLayoutInflaterFactory] on the given LayoutInflater.
     */
    private fun installFactory2(inflater: LayoutInflater, anylocale: AnylocaleAndroid) {
        val existingFactory = inflater.factory
        val existingFactory2 = inflater.factory2
        inflater.factory2 = AnylocaleLayoutInflaterFactory(anylocale, existingFactory, existingFactory2)
    }

    companion object {
        /**
         * Wraps the given [base] context with a [AnylocaleContextWrapper] that uses the global singleton instance of Anylocale.
         *
         * This method is a convenience function for cases where `attachBaseContext` provides a nullable context.
         * If [base] is `null`, it falls back to returning a regular [ContextWrapper].
         *
         * @param base The context to wrap, which may be `null`.
         * @return A wrapped [ContextWrapper] with Anylocale support, or a regular [ContextWrapper] if Anylocale is unavailable.
         */
        @JvmStatic
        fun wrap(base: Context?): ContextWrapper {
            val anylocale = Anylocale.instanceOrNull ?: return ContextWrapper(base)
            return wrap(base, anylocale)
        }

        /**
         * Wraps the given [base] context with a [AnylocaleContextWrapper] using the provided [anylocale] instance.
         *
         * This method allows explicitly specifying a Anylocale instance for cases where dependency injection
         * or multiple Anylocale instances are needed.
         * If [base] is `null`, it falls back to returning a regular [ContextWrapper].
         *
         * @param base The context to wrap, which may be `null`.
         * @param anylocale The Anylocale translation service instance to use for retrieving localized strings.
         * @return A wrapped [ContextWrapper] with Anylocale support, or a regular [ContextWrapper] if [base] is `null`.
         */
        @JvmStatic
        fun wrap(base: Context?, anylocale: Anylocale): ContextWrapper {
            if (base == null) {
                return ContextWrapper(base)
            }
            return AnylocaleContextWrapper(base, anylocale)
        }
    }
}