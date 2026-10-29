plugins {
    id("com.android.library")
}
android {
    namespace = "com.oem.car.middleware.vehicle"
    compileSdk = 37
    defaultConfig { minSdk = 28 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
dependencies {
    implementation(project(":core:aidl"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
}
