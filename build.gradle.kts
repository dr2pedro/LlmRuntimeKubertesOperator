plugins {
    kotlin("jvm") version "2.2.21"
}

group = "com.codeplaydata"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javaoperatorsdk:operator-framework:5.2.1")
    implementation("io.fabric8:kubernetes-client:7.4.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}