import org.gradle.api.Plugin
import org.gradle.api.Project
import util.testImplementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingTestUnitPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            testImplementations("junit", "kotlinx-coroutines-test", "turbine")
        }
    }
}
