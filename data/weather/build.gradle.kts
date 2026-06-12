plugins {
    alias(libs.plugins.flexing.ktor)
    alias(libs.plugins.flexing.koin)
}

android {
    namespace = "shvyn22.flexingactivities.data.weather"
}

dependencies {
    implementation(projects.domain.weather)
    implementation(projects.data.core)
}