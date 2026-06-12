package util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.plugins(
    vararg alias: String
) = with(pluginManager) {
    alias.forEach { pluginId ->
        apply(libs.getPlugin(pluginId))
    }
}

fun Project.implementations(
    vararg alias: String
) {
    dependencies {
        alias.forEach { add("implementation", libs.getLibrary(it)) }
    }
}

fun Project.platformImplementations(
    alias: String
) {
    dependencies {
        add("implementation", platform(libs.getLibrary(alias)))
    }
}

fun Project.debugImplementations(
    vararg alias: String
) {
    dependencies {
        alias.forEach { add("debugImplementation", libs.getLibrary(it)) }
    }
}

fun Project.testImplementations(
    vararg alias: String
) {
    dependencies {
        alias.forEach { add("testImplementation", libs.getLibrary(it)) }
    }
}

fun Project.androidTestImplementations(
    vararg alias: String
) {
    dependencies {
        alias.forEach { add("androidTestImplementation", libs.getLibrary(it)) }
    }
}

fun Project.androidTestPlatformImplementations(
    alias: String
) {
    dependencies {
        add("androidTestImplementation", platform(libs.getLibrary(alias)))
    }
}

fun Project.ksp(
    vararg alias: String
) {
    dependencies {
        alias.forEach { add("ksp", libs.getLibrary(it)) }
    }
}

fun VersionCatalog.getLibrary(
    alias: String
) = findLibrary(alias).get()

fun VersionCatalog.getPlugin(
    alias: String
) = findPlugin(alias).get().get().pluginId

fun Project.androidApplication(
    action: Action<ApplicationExtension>
) {
    extensions.configure(ApplicationExtension::class.java, action)
}

fun Project.androidLibrary(
    action: Action<LibraryExtension>
) {
    extensions.configure(LibraryExtension::class.java, action)
}

fun Project.java(
    action: Action<JavaPluginExtension>
) {
    extensions.configure(JavaPluginExtension::class.java, action)
}

fun Project.kotlinJvm(
    action: Action<KotlinJvmProjectExtension>
) {
    extensions.configure(KotlinJvmProjectExtension::class.java, action)
}

fun Project.kotlin(
    action: Action<KotlinProjectExtension>
) {
    extensions.configure(KotlinProjectExtension::class.java, action)
}