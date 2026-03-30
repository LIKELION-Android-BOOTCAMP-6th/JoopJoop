plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

    // 보안 플러그인 적용 (local.properties를 자동으로 읽습니다)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.joopjoop"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.joopjoop"
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 구글 맵 & 위치 관련 라이브러리
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Compose에서 ViewModel을 사용하기 위한 필수 라이브러리
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // Geohash 생성을 위한 Firebase 공식 라이브러리 (Geofire)
    implementation("com.firebase:geofire-android-common:3.2.0")

    // Jetpack Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 파이어베이스 관련 라이브러리
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-storage-ktx")
    // 사용할 서비스들
    implementation("com.google.firebase:firebase-auth-ktx")     // 인증용
    implementation("com.google.firebase:firebase-firestore-ktx") // 데이터베이스용
    // Coroutines를 Firebase와 함께 쓰기 위한 라이브러리 (await() 사용 위함)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Coil 기본 및 Compose 지원
    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 네비게이션을 위한 라이브러리
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // WorkManager 라이브러리 추가 (CoroutineWorker 사용 가능)
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}