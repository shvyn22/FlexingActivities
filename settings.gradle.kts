pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "FlexingActivities"

fun includeModule(path: String) {
    include(path)
    val dir = path.removePrefix(":").replace(":", "/")
    project(path).projectDir = file(dir)
}

include(":app")

// Domain layer
includeModule(":domain:core")
includeModule(":domain:geocoding")
includeModule(":domain:weather")
includeModule(":domain:favorites")

// Data layer
includeModule(":data:core")
includeModule(":data:geocoding")
includeModule(":data:weather")
includeModule(":data:favorites")

// UI / Feature layer
includeModule(":core-ui")
includeModule(":feature:core")
includeModule(":feature:search")
includeModule(":feature:favorites")
includeModule(":feature:details")
