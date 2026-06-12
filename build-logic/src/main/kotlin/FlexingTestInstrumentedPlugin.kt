import org.gradle.api.Plugin
import org.gradle.api.Project
import util.androidTestImplementations
import util.androidTestPlatformImplementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingTestInstrumentedPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            androidTestPlatformImplementations("androidx-compose-bom")
            androidTestImplementations(
                "androidx-compose-ui-test-junit4",
                "androidx-junit",
                "androidx-espresso-core"
            )
        }
    }
}
