plugins {
    alias(libs.plugins.flexing.android.library.compose)
    alias(libs.plugins.flexing.koin)
    alias(libs.plugins.flexing.test.unit)
    alias(libs.plugins.flexing.test.instrumented)
}

android {
    namespace = "shvyn22.flexingactivities.feature.search"
}

dependencies {
    implementation(projects.domain.core)
    implementation(projects.domain.geocoding)
    implementation(projects.feature.core)
    implementation(projects.coreUi)
    testImplementation(testFixtures(projects.domain.geocoding))
}
