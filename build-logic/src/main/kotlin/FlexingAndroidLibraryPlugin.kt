import org.gradle.api.Plugin
import org.gradle.api.Project
import util.COMPILE_SDK
import util.JAVA_VERSION
import util.MIN_SDK
import util.androidLibrary
import util.plugins
import util.implementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins("android-library")

            androidLibrary {
                compileSdk = COMPILE_SDK
                defaultConfig {
                    minSdk = MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = JAVA_VERSION
                    targetCompatibility = JAVA_VERSION
                }
            }

            implementations("kotlinx-coroutines-android")
        }
    }
}
