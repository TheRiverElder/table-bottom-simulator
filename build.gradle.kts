
plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.5.0"
    application
}

group = "io.github.theriverelder.minigames"
version = "1.0-SNAPSHOT"
//val ktor_version = "2.3.8"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("io.ktor:ktor-server-core:$ktor_version")
//    implementation("io.ktor:ktor-server-netty:$ktor_version")
//    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.java-websocket:Java-WebSocket:1.5.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

//kotlin {
//    jvmToolchain(8)
//}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("io.github.theriverelder.minigames.tablebottomsimulator.MainKt")
}