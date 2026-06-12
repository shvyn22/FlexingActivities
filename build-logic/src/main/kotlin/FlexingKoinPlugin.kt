import org.gradle.api.Plugin
import org.gradle.api.Project
import util.implementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingKoinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            implementations("koin-core")
            if (pluginManager.hasPlugin("com.android.base")) {
                implementations("koin-android")
            }
        }
    }
}
