plugins {
    alias(libs.plugins.flexing.android.application)
    alias(libs.plugins.flexing.android.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "shvyn22.flexingactivities"
    defaultConfig {
        applicationId = "shvyn22.flexingactivities"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
}

dependencies {
    // Project modules (type-safe accessors: :domain:core → projects.domain.core)
    implementation(projects.domain.core)
    implementation(projects.domain.geocoding)
    implementation(projects.domain.weather)
    implementation(projects.domain.favorites)
    implementation(projects.data.core)
    implementation(projects.data.geocoding)
    implementation(projects.data.weather)
    implementation(projects.data.favorites)
    implementation(projects.coreUi)
    implementation(projects.feature.core)
    implementation(projects.feature.search)
    implementation(projects.feature.favorites)
    implementation(projects.feature.details)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)
}

tasks.register("allUnitTests") {
    description = "Runs unit tests across all modules"
    group = "verification"
    rootProject.subprojects.forEach { subproject ->
        dependsOn(subproject.tasks.matching { it.name == "testDebugUnitTest" })
    }
}

tasks.register("allUiTests") {
    description = "Runs instrumented UI tests across all modules"
    group = "verification"
    rootProject.subprojects.forEach { subproject ->
        dependsOn(subproject.tasks.matching { it.name == "connectedDebugAndroidTest" })
    }
}
