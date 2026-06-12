import org.gradle.api.Plugin
import org.gradle.api.Project
import util.plugins
import util.implementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingKtorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins(
                "flexing-android-library",
                "kotlin-serialization"
            )

            implementations(
                "ktor-client-core",
                "ktor-client-okhttp",
                "ktor-client-content-negotiation",
                "ktor-serialization-json",
                "ktor-client-logging",
                "kotlinx-serialization-json"
            )
        }
    }
}
