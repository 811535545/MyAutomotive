plugins {
    id("com.android.library")
}
android {
    namespace = "com.oem.car.core.uxrestrictions"
    compileSdk = 37
    defaultConfig { minSdk = 28 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        // CarUxRestrictionsEngine 会调用 CarLogger -> android.util.Log；
        // JVM 单测里让 Android 方法返回默认值，避免 "Method not mocked"。
        unitTests.isReturnDefaultValues = true
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
    implementation(libs.kotlinx.coroutines.core)
    testImplementation("junit:junit:4.13.2")
}
