plugins {
    id("java")
}

group = "org.thingai"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.xerial:sqlite-jdbc:3.43.2.0")
    implementation("org.slf4j:slf4j-api:2.0.9") // Logging interface of jdbc
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    implementation("io.javalin:javalin:6.7.0")
    implementation("org.jmdns:jmdns:3.5.12")

    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation(files("libs/applicationbase.jar"))
    implementation(files("libs/aibase.jar"))
}

tasks.test {
    useJUnitPlatform()
}