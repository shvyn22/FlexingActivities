plugins {
    alias(libs.plugins.flexing.kotlin.library)
    alias(libs.plugins.flexing.koin)
    alias(libs.plugins.flexing.test.unit)
    `java-test-fixtures`
}

dependencies {
    implementation(projects.domain.core)
}