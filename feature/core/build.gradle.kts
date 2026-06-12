plugins {
    alias(libs.plugins.flexing.android.library.compose)
}

android {
    namespace = "shvyn22.flexingactivities.feature.core"
}

dependencies {
    implementation(projects.domain.core)
}