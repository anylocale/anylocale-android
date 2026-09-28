package com.anylocale

import com.anylocale.common.anylocaleExtension
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class AnylocaleCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider {
            val config = kotlinCompilation.target.project.anylocaleExtension.compilerPlugin

            listOf(
                SubpluginOption("anylocale.android.getString", config.android.replaceGetString.getOrElse(true).toString()),
                SubpluginOption("anylocale.android.pluralString", config.android.replacePluralString.getOrElse(true).toString()),
                SubpluginOption("anylocale.compose.stringResource", config.compose.replaceStringResource.getOrElse(true).toString()),
                SubpluginOption("anylocale.compose.pluralStringResource", config.compose.replacePluralStringResource.getOrElse(true).toString())
            )
        }
    }

    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = GROUP_NAME,
            artifactId = ARTIFACT,
            version = AnylocalePlugin.version
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    companion object {
        private const val GROUP_NAME = "com.anylocale"
        private const val ARTIFACT = "sdk-compiler-plugin"
        private const val PLUGIN_ID = "com.anylocale.sdk.compiler-plugin"
    }
}