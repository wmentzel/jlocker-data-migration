plugins {
    kotlin("jvm") version "1.6.20"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

version = "1.0-SNAPSHOT"

dependencies {

    // This is needed because code which uses jGraphT is copied along
    // leave at 1.0.0, breaking changes
    implementation("org.jgrapht:jgrapht-ext:1.0.0")
    implementation("org.jgrapht:jgrapht-core:1.0.0")

    // Don't bump those! Higher versions will not be compatible with Java 7.
    implementation("com.google.code.gson:gson:2.3.1")
    implementation("com.google.guava:guava:20.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.0")
    testImplementation(kotlin("test"))
}

tasks {

    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.6" // There is no 1.7
    }

    compileJava {
        sourceCompatibility = "1.7"
        targetCompatibility = "1.7"
    }

    "wrapper"(Wrapper::class) {
        gradleVersion = "7.0"
    }

    // for shipping
    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = "main.MainKt"
        }
        archiveBaseName.set(project.name)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get())
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
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