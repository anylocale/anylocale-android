import com.anylocale.AnylocalePluginExtension
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertTrue

class AnylocaleTest {
    @Test
    fun `applying the plugin registers the anylocale extension with conventions`() {
        val project = ProjectBuilder.builder().build()

        project.pluginManager.apply("com.anylocale.sdk")

        val extension = project.extensions.getByName("anylocale") as AnylocalePluginExtension
        assertTrue(extension.compilerPlugin.android.replaceGetString.get())
        assertTrue(extension.compilerPlugin.compose.replaceStringResource.get())
    }
}
