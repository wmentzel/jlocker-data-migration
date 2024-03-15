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
    implementation("com.google.code.gson:gson:2.3.1")
    implementation("com.google.guava:guava:23.0")
    implementation("org.jgrapht:jgrapht-ext:1.0.0")
    implementation("org.jgrapht:jgrapht-core:1.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.0")

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("org.mockito:mockito-inline:4.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.7.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
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