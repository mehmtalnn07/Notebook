// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val room_version = "2.6.1"
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    kotlin("jvm") version "1.9.21" apply false
    id("androidx.room") version "$room_version" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.21"))
    }
}