plugins {
    alias(libs.plugins.flexing.kotlin.library)
    alias(libs.plugins.flexing.koin)
    alias(libs.plugins.flexing.test.unit)
    `java-test-fixtures`
}

dependencies {
    implementation(projects.domain.core)
    implementation(projects.domain.weather)
    testFixturesImplementation(libs.kotlinx.coroutines.core)
    testImplementation(testFixtures(projects.domain.weather))
}