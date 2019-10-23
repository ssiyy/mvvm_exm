import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)

     defaultConfig {
        applicationId = "com.siy.mvvm.exm"
        minSdkVersion(17)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.schemaLocation" to "$projectDir/schemas"))
            }
        }
    }

    signingConfigs {
        create("releaseKey") {
            keyAlias = "mvvm_exm"
            keyPassword = "mvvm_exm"
            storeFile = file("${System.getProperty("user.dir")}\\mvvm_exm.jks")
            storePassword = "mvvm_exm"
            isV2SigningEnabled = false
        }

        create("debugKey") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
            storeFile = file("${System.getenv("ANDROID_SDK_HOME")}\\.android\\debug.keystore")
            isV2SigningEnabled = false
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("releaseKey")
            isZipAlignEnabled = true
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debugKey")
            isZipAlignEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        val option = this as KotlinJvmOptions
        option.jvmTarget = "1.8"
    }

    dataBinding {
        isEnabled = true
    }
}


dependencies {

    val kotlinVersion: String by rootProject.extra
    val navVersion: String by rootProject.extra

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.1")

    //rx 权限管理
    // implementation("com.github.tbruyelle:rxpermissions:0.10.2")
   //implementation("com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2")


    //kotlin
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.2.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha05")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha05")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha05")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.2.0-alpha05")
    implementation("android.arch.lifecycle:runtime:2.1.0-alpha04")
    implementation("android.arch.lifecycle:common-java8:2.1.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0-alpha05")
    kapt("androidx.lifecycle:lifecycle-compiler:2.2.0-alpha05")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //page
    implementation("androidx.paging:paging-runtime:2.1.0")
    implementation("androidx.paging:paging-rxjava2:2.1.0")

    //room
    implementation("androidx.room:room-runtime:2.2.0-rc01")
    implementation("androidx.room:room-rxjava2:2.2.0-rc01")
    implementation("androidx.room:room-ktx:2.2.0-rc01")
    kapt("androidx.room:room-compiler:2.2.0-rc01")

    //dagger
    implementation("com.google.dagger:dagger-android:2.22.1")
    implementation("com.google.dagger:dagger-android-support:2.22.1")
    kapt("com.google.dagger:dagger-compiler:2.22.1")
    kapt("com.google.dagger:dagger-android-processor:2.22.1")

    //retrofit2 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.11.0")

    //rxjava内存管理
    implementation("com.uber.autodispose:autodispose:1.1.0")
    implementation("com.uber.autodispose:autodispose-android-archcomponents:1.1.0")

    //pickerView
    implementation("com.contrarywind:Android-PickerView:4.1.7")
    //loading
    implementation("com.billy.android:gloading:1.0.1")
    //日志
    implementation("com.jakewharton.timber:timber:4.7.1")
    //recyclerView
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta04")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50")

    //图片压缩
    implementation("top.zibin:Luban:1.1.8")

    //Kottlin版本的Rxbinding
    implementation("ru.ldralighieri.corbind:corbind:1.2.0")

    implementation ("com.github.bumptech.glide:glide:4.8.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.3.1")
    kapt("com.github.bumptech.glide:compiler:4.8.0")
}
