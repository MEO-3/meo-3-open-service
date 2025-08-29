plugins {
    id("java")
}

group = "org.thingai"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("../libs/applicationbase.jar"))
    compileOnly(files("../libs/yguard-4.1.1.jar"))

    implementation("org.xerial:sqlite-jdbc:3.43.2.0")
    implementation("org.slf4j:slf4j-api:2.0.9") // Logging interface of jdbc
    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    implementation("io.javalin:javalin:6.7.0")
    implementation("org.jmdns:jmdns:3.5.12")
}

tasks.test {
    useJUnitPlatform()
}