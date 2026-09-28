package com.anylocale.common

import com.anylocale.AnylocalePluginExtension
import dev.datlag.tooling.async.scopeCatching
import org.gradle.api.Project

internal val Project.anylocaleExtension: AnylocalePluginExtension
    get() = this.extensions.findByType(AnylocalePluginExtension::class.java)
        ?: scopeCatching { createAnylocaleExtension() }.getOrNull()
        ?: this.extensions.getByType(AnylocalePluginExtension::class.java)

@Throws(IllegalArgumentException::class)
private fun Project.createAnylocaleExtension(): AnylocalePluginExtension {
    return this@createAnylocaleExtension.extensions.create(
        "anylocale",
        AnylocalePluginExtension::class.java
    ).apply { setupConvention(this@createAnylocaleExtension) }
}