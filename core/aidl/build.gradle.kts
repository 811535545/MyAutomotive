plugins {
    id("com.android.library")
}
android {
    namespace = "com.oem.car.middleware.aidl"
    compileSdk = 37
    defaultConfig { minSdk = 28 }
    buildFeatures { aidl = true }
}
