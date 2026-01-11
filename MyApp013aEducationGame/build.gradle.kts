// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Použijeme nejnovější stabilní verzi Android Gradle Pluginu, pokud ji chcete specifikovat zde.
    alias(libs.plugins.android.application) apply false

    // OPRAVA 1: Aktualizace na nejnovější stabilní verzi Kotlinu.
    alias(libs.plugins.kotlin.android) version "2.0.0" apply false

    // OPRAVA 2: Použití verze KSP, která je ověřená a kompatibilní s Kotlinem 2.0.0.
    // Tato verze zaručeně existuje v repozitářích.
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}
