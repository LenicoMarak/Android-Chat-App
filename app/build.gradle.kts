plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("io.objectbox")
}


    namespace = "com.application.chat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.application.chat"
        minSdk = 20
        targetSdk = 33
        versionCode = 8
        versionName = "1.7"
        multiDexEnabled=true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release"){
            isMinifyEnabled=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies{
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    //implementation("com.google.firebase:firebase-core:21.1.1")
    // implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")
    //implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //implementation("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.github.krokyze:ucropnedit:2.2.8")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("io.objectbox:objectbox-android:3.7.1")
}