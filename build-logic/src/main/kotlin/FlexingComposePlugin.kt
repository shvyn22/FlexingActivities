import org.gradle.api.Plugin
import org.gradle.api.Project
import util.androidLibrary
import util.plugins
import util.debugImplementations
import util.implementations
import util.platformImplementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins(
                "flexing-android-library",
                "kotlin-compose"
            )

            androidLibrary {
                buildFeatures { compose = true }
            }

            platformImplementations("androidx-compose-bom")
            implementations(
                "androidx-compose-ui",
                "androidx-compose-ui-graphics",
                "androidx-compose-ui-tooling-preview",
                "androidx-compose-material3",
                "androidx-compose-material-icons-core",
                "androidx-lifecycle-viewmodel-compose",
                "androidx-lifecycle-runtime-compose",
                "koin-androidx-compose"
            )
            debugImplementations(
                "androidx-compose-ui-tooling",
                "androidx-compose-ui-test-manifest"
            )
        }
    }
}
