plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("flexingAndroidApplication") {
            id = "flexing.android.application"
            implementationClass = "FlexingAndroidApplicationPlugin"
        }
        register("flexingAndroidLibrary") {
            id = "flexing.android.library"
            implementationClass = "FlexingAndroidLibraryPlugin"
        }
        register("flexingAndroidLibraryCompose") {
            id = "flexing.android.library.compose"
            implementationClass = "FlexingComposePlugin"
        }
        register("flexingKotlinLibrary") {
            id = "flexing.kotlin.library"
            implementationClass = "FlexingKotlinLibraryPlugin"
        }
        register("flexingAndroidRoom") {
            id = "flexing.android.room"
            implementationClass = "FlexingAndroidRoomPlugin"
        }
        register("flexingKoin") {
            id = "flexing.koin"
            implementationClass = "FlexingKoinPlugin"
        }
        register("flexingKtor") {
            id = "flexing.ktor"
            implementationClass = "FlexingKtorPlugin"
        }
        register("flexingUtils") {
            id = "flexing.utils"
            implementationClass = "FlexingUtilsPlugin"
        }
        register("flexingTestUnit") {
            id = "flexing.test.unit"
            implementationClass = "FlexingTestUnitPlugin"
        }
        register("flexingTestInstrumented") {
            id = "flexing.test.instrumented"
            implementationClass = "FlexingTestInstrumentedPlugin"
        }
    }
}
