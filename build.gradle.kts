plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application

}
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
application {
    mainClass.set("financetracker.FinanceTrackerKt") // ✅ Your main class with `main()`
}



tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}