// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {
    val kotlinVersion by extra("1.3.60")
    val navVersion by extra("2.2.0-alpha01")

    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.2")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}


task<Exec>("keystore") {
    group = "mvvm"
    description = "获取证书信息"

    commandLine = listOf("cmd", "/c", "keytool -list -v -keystore ${System.getProperty("user.dir")}\\mvvm_exm.jks -storepass mvvm_exm")
//    commandLine = listOf("cmd", "/c", "keytool -list -v -keystore ${System.getenv("ANDROID_SDK_HOME")}\\.android\\debug.keystore -storepass android")

    doLast {
        val outputStr = standardOutput.toString()
        println(outputStr)
    }
}


task<Exec>("showDependencies") {
    group = "mvvm"
    description = "依赖信息"

    commandLine = listOf("cmd", "/c", "gradlew :app:dependencies --configuration releaseCompileClasspath")

    doLast {
        val outputStr = standardOutput.toString()
        println(outputStr)
    }
}