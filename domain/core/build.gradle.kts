plugins {
    alias(libs.plugins.flexing.kotlin.library)
    alias(libs.plugins.flexing.koin)
    alias(libs.plugins.flexing.test.unit)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.ktor.client.mock)
}
