plugins {
    alias(libs.plugins.flexing.ktor)
    alias(libs.plugins.flexing.koin)
}

android {
    namespace = "shvyn22.flexingactivities.data.geocoding"
}

dependencies {
    implementation(projects.domain.geocoding)
    implementation(projects.data.core)
}
