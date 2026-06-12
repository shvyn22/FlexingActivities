import org.gradle.api.Plugin
import org.gradle.api.Project
import util.plugins
import util.implementations
import util.ksp

@Suppress("unused") // reason: used as a convention plugin
class FlexingAndroidRoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins("ksp")

            implementations(
                "room-runtime",
                "room-ktx"
            )

            ksp("room-compiler")
        }
    }
}
