plugins {
    kotlin("jvm") version "1.6.20"
}

repositories {
    mavenCentral()
}

dependencies {

    // This is needed because code which uses jGraphT is copied along
    implementation("org.jgrapht:jgrapht-ext:1.0.0") // leave at 1.0.0, breaking changes
    implementation("org.jgrapht:jgrapht-core:1.0.0") // leave at 1.0.0, breaking changes

    // Don't bump those! Higher versions will not be compatible with Java 7.
    implementation("com.google.code.gson:gson:2.3.1")
    implementation("com.google.guava:guava:23.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.0")
    testImplementation(kotlin("test"))
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