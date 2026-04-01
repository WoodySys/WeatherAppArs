plugins {
    // Android Gradle Plugin (AGP) - должен быть 8.2.0 или выше
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false

    // Kotlin - должен быть 1.9.0 или выше, чтобы не искать HasConvention
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}