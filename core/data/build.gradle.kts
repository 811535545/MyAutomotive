plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.oem.car.core.data"
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
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:vehicle-service"))
    implementation(project(":core:uxrestrictions"))
    implementation(project(":core:power"))
    implementation(project(":core:audio"))
    implementation(project(":core:digitalkey"))
    implementation(project(":core:telematics"))
    implementation(project(":core:adas"))
    implementation(project(":core:account"))
    implementation(project(":core:navigation"))
    implementation(project(":core:phone"))
    implementation(project(":core:energy"))
    implementation(project(":core:health"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
