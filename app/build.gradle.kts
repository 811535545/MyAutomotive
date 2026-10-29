plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.oem.car.app"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.oem.car.app"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


dependencies {

    //基础配置
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:audio"))
    implementation(project(":core:vehicle-service"))
    implementation(project(":core:vehicle"))
    implementation(project(":core:uxrestrictions"))
    implementation(project(":core:power"))
    implementation(project(":core:digitalkey"))
    implementation(project(":core:telematics"))
    implementation(project(":core:adas"))
    implementation(project(":core:account"))
    implementation(project(":core:navigation"))
    implementation(project(":core:phone"))
    implementation(project(":core:energy"))
    implementation(project(":core:health"))
    implementation(project(":core:aidl"))

    // Feature modules
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:cluster"))
    implementation(project(":feature:climate"))
    implementation(project(":feature:media"))
    implementation(project(":feature:connected"))
    implementation(project(":feature:navigation"))
    implementation(project(":feature:phone"))
    implementation(project(":feature:energy"))
    implementation(project(":feature:health"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:voice"))
}