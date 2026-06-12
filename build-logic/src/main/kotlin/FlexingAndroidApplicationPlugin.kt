import org.gradle.api.Plugin
import org.gradle.api.Project
import util.COMPILE_SDK
import util.JAVA_VERSION
import util.MIN_SDK
import util.TARGET_SDK
import util.androidApplication
import util.plugins
import util.implementations
import util.platformImplementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins(
                "android-application",
                "kotlin-compose",
                "flexing-koin",
            )

            androidApplication {
                compileSdk = COMPILE_SDK
                defaultConfig {
                    minSdk = MIN_SDK
                    targetSdk = TARGET_SDK
                }
                compileOptions {
                    sourceCompatibility = JAVA_VERSION
                    targetCompatibility = JAVA_VERSION
                }
                buildFeatures {
                    compose = true
                }
            }

            platformImplementations("androidx-compose-bom")
            implementations(
                "androidx-activity-compose",
                "androidx-compose-ui",
                "androidx-navigation-compose",
            )
        }
    }
}
