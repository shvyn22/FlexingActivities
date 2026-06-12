import org.gradle.api.Plugin
import org.gradle.api.Project
import util.JAVA_VERSION
import util.JVM_TARGET
import util.java
import util.kotlinJvm
import util.plugins
import util.implementations

@Suppress("unused") // reason: used as a convention plugin
class FlexingKotlinLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins("kotlin-jvm")

            java {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
            }

            kotlinJvm {
                compilerOptions {
                    jvmTarget.set(JVM_TARGET)
                }
            }

            implementations("kotlinx-coroutines-core")
        }
    }
}
