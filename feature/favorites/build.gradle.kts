plugins {
    alias(libs.plugins.flexing.android.library.compose)
    alias(libs.plugins.flexing.koin)
    alias(libs.plugins.flexing.test.unit)
    alias(libs.plugins.flexing.test.instrumented)
}

android {
    namespace = "shvyn22.flexingactivities.feature.favorites"
}

dependencies {
    implementation(projects.domain.core)
    implementation(projects.domain.favorites)
    implementation(projects.feature.core)
    implementation(projects.coreUi)
    testImplementation(testFixtures(projects.domain.weather))
    testImplementation(testFixtures(projects.domain.favorites))
}
