plugins {
    kotlin("jvm") version "1.6.20"
    java
    application
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("com.google.code.gson:gson:2.3.1")
    implementation("com.google.guava:guava:20.0")
    implementation("org.jgrapht:jgrapht-ext:1.0.0")
    implementation("org.jgrapht:jgrapht-core:1.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
}

tasks {

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

        println(file(src))
        println(file(destination))

        copy {
            from(src)
            into(destination)
        }
    }
}