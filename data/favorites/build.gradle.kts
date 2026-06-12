plugins {
    alias(libs.plugins.flexing.android.library)
    alias(libs.plugins.flexing.koin)
}

android {
    namespace = "shvyn22.flexingactivities.data.favorites"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(projects.data.core)
    implementation(projects.domain.core)
    implementation(projects.domain.favorites)
}
