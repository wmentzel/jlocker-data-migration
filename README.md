This project is needed in order to create a Java 7 compatible JAR file which can read jLocker data serialized with 
Java 7 (binary). This data is then written as JSON file which has no restrictions when being read.

## Build JAR

1. `cd migration`
2. `./gradlew copyStuff`
3. `./gradlew fatJar`
4. Ship migration/build/libs/migration-1.0-SNAPSHOT.jar

## Run tests

1. Copy migration/src/main/resources/jlocker.dat to migration/build/classes/kotlin
2. Run "Migrate test DAT to JSON" (IntelliJ run configuration)
3. Copy migration/build/classes/kotlin/jlocker.json to migration-test/src/test/resources
4. `cd migration-test`
5. `./gradlew copyStuff`
6. `./gradlew test`