plugins {
    kotlin("jvm") version "1.6.20"
    application
    java
    `maven-publish`
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.randomlychosenbytes.jlocker.MainKt")
}

dependencies {

    // This is needed because code which uses jGraphT is copied along
    implementation("org.jgrapht:jgrapht-ext:1.0.0") // leave at 1.0.0, breaking changes
    implementation("org.jgrapht:jgrapht-core:1.0.0") // leave at 1.0.0, breaking changes

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks {
    "wrapper"(Wrapper::class) {
        gradleVersion = "7.0"
    }

    register("copyStuff") {

        val src = "${project.buildDir}/../../../jlocker/src/main/java/com/randomlychosenbytes/jlocker"
        val destination = "${project.buildDir}/../src/main/java/com/randomlychosenbytes/jlocker"

        println("Copying ${file(src)} to ${file(destination)}")

        copy {
            from(src)
            into(destination)
        }
    }
}