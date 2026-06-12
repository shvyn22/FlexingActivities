plugins {
    alias(libs.plugins.flexing.android.library.compose)
}

android {
    namespace = "shvyn22.flexingactivities.coreui"
}

dependencies {
    implementation(projects.domain.core)
}
