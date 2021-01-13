plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.4.20"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:20.0")

    implementation("com.randomlychosenbytes:jlocker:1.6")

    implementation("org.jgrapht:jgrapht-ext:0.9.0")
    implementation("org.jgrapht:jgrapht-core:0.9.0")


    testImplementation("junit:junit:4.13.1")
    testImplementation("org.junit.platform:junit-platform-surefire-provider:1.2.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(7))
    }
}
