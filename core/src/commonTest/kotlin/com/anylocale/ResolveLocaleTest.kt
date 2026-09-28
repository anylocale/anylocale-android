package com.anylocale

import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.forLocaleTag
import de.comahe.i18n4k.toTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResolveLocaleTest {

    /**
     * Testable subclass that exposes resolveLocale for testing.
     */
    private class TestableAnylocale(config: Anylocale.Config) : Anylocale(config) {
        fun testResolveLocale(locale: Locale?): Locale? = resolveLocale(locale)
    }

    private fun createAnylocale(
        availableLocales: List<Locale>,
        defaultLanguage: Locale? = null,
    ): TestableAnylocale {
        val config = Anylocale.Config.Builder()
            .availableLocales(availableLocales)
            .apply { defaultLanguage?.let { defaultLanguage(it) } }
            .build()
        return TestableAnylocale(config)
    }

    @Test
    fun exactMatchWorks() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("zh-Hans")))
        val result = anylocale.testResolveLocale(forLocaleTag("zh-Hans"))
        assertEquals("zh-Hans", result?.toTag("-"))
    }

    @Test
    fun caseInsensitiveMatchForScript() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("zh-Hans")))
        val result = anylocale.testResolveLocale(forLocaleTag("zh-hans"))
        assertEquals("zh-Hans", result?.toTag("-"))
    }

    @Test
    fun caseInsensitiveMatchForRegion() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("pt-BR")))
        val result = anylocale.testResolveLocale(forLocaleTag("pt-br"))
        assertEquals("pt-BR", result?.toTag("-"))
    }

    @Test
    fun fallbackFromRegionToBaseLanguage() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("pt")))
        val result = anylocale.testResolveLocale(forLocaleTag("pt-BR"))
        assertEquals("pt", result?.toTag("-"))
    }

    @Test
    fun fallbackFromScriptRegionToScript() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("zh-Hans")))
        val result = anylocale.testResolveLocale(forLocaleTag("zh-Hans-CN"))
        assertEquals("zh-Hans", result?.toTag("-"))
    }

    @Test
    fun fallsBackToDefaultLanguageWhenNoMatch() {
        val defaultLang = forLocaleTag("en")
        val anylocale = createAnylocale(
            listOf(forLocaleTag("en"), forLocaleTag("fr")),
            defaultLanguage = defaultLang,
        )
        val result = anylocale.testResolveLocale(forLocaleTag("ja"))
        assertEquals("en", result?.toTag("-"))
    }

    @Test
    fun nullLocaleReturnsNull() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en")))
        assertNull(anylocale.testResolveLocale(null))
    }

    @Test
    fun returnsInputLocaleWhenNoAvailableLocales() {
        val config = Anylocale.Config.Builder().build()
        val anylocale = TestableAnylocale(config)
        val locale = forLocaleTag("zh-Hans")
        val result = anylocale.testResolveLocale(locale)
        assertEquals("zh-Hans", result?.toTag("-"))
    }

    @Test
    fun unicodeExtensionFallsBackToBaseLanguage() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("es")))
        val result = anylocale.testResolveLocale(forLocaleTag("es-u-ms-metric"))
        assertEquals("es", result?.toTag("-"))
    }

    @Test
    fun unicodeExtensionExactMatchIsPreferredOverBaseLanguage() {
        val anylocale = createAnylocale(listOf(forLocaleTag("es"), forLocaleTag("es-u-ms-metric")))
        val result = anylocale.testResolveLocale(forLocaleTag("es-u-ms-metric"))
        assertEquals("es-u-ms-metric", result?.toTag("-"))
    }

    @Test
    fun unicodeExtensionOnRegionalLocaleFallsBackToRegion() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("en-US")))
        val result = anylocale.testResolveLocale(forLocaleTag("en-US-u-ms-metric"))
        assertEquals("en-US", result?.toTag("-"))
    }

    @Test
    fun fallbackFromScriptRegionToRegion() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en"), forLocaleTag("en-US")))
        val result = anylocale.testResolveLocale(forLocaleTag("en-Latn-US"))
        assertEquals("en-US", result?.toTag("-"))
    }

    @Test
    fun scriptMatchIsPreferredOverRegionMatch() {
        val anylocale = createAnylocale(listOf(forLocaleTag("en-Latn"), forLocaleTag("en-US")))
        val result = anylocale.testResolveLocale(forLocaleTag("en-Latn-US"))
        assertEquals("en-Latn", result?.toTag("-"))
    }

    @Test
    fun regionMatchCanCrossScripts() {
        val anylocale = createAnylocale(listOf(forLocaleTag("zh-CN"), forLocaleTag("zh-TW")))
        val result = anylocale.testResolveLocale(forLocaleTag("zh-Hant-CN"))
        assertEquals("zh-CN", result?.toTag("-"))
    }

    @Test
    fun fallbackFromScriptRegionVariantToScriptRegion() {
        val anylocale = createAnylocale(listOf(forLocaleTag("ca"), forLocaleTag("ca-Latn-ES")))
        val result = anylocale.testResolveLocale(forLocaleTag("ca-Latn-ES-valencia"))
        assertEquals("ca-Latn-ES", result?.toTag("-"))
    }

    @Test
    fun fallbackFromScriptRegionVariantToRegionVariant() {
        val anylocale = createAnylocale(listOf(forLocaleTag("ca"), forLocaleTag("ca-ES-valencia")))
        val result = anylocale.testResolveLocale(forLocaleTag("ca-Latn-ES-valencia"))
        assertEquals("ca-ES-valencia", result?.toTag("-"))
    }
}
