plugins {
    id("com.android.library")
}
android {
    namespace = "com.oem.car.core.vehicleservice"
    compileSdk = 37
    useLibrary("android.car")
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
    compileOnly(files("libs/android.car-stubs.jar"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:aidl"))
    implementation(project(":core:audio"))
    implementation(project(":core:navigation"))
    implementation(project(":core:power"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
