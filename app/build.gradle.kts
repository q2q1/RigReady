plugins {
    alias(libs.plugins.android.application)
    // Firebase 配置需要 google-services 插件（请确保把 Firebase 控制台下载的 google-services.json 放到 app/ 目录）
    id("com.google.gms.google-services") version "4.4.4"
}

android {
    namespace = "com.example.rig"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.rig"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // UI 列表与页面
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // 图片加载（用于展示 Firebase Storage 图片）
    implementation("com.github.bumptech.glide:glide:5.0.5")

    // Firebase（使用 BoM 统一管理版本）
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Stripe + networking (publishable key only in client)
    implementation("com.stripe:stripe-android:20.3.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Volley (used by Stripe sample MainActivity2 you provided)
    implementation("com.android.volley:volley:1.2.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}