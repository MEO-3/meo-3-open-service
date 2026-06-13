plugins {
    id("java")
    id("application")
}

group = "org.thingai.app"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.thingai.app.meo.Main")
}

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.xerial:sqlite-jdbc:3.43.2.0")
    implementation("org.slf4j:slf4j-api:2.0.9") // Logging interface of jdbc
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("io.javalin:javalin:7.2.0")
    implementation("org.jmdns:jmdns:3.5.12")

    implementation("com.zaxxer:HikariCP:5.1.0")

    // mqtt client
    implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:1.2.5")

    implementation(files("libs/applicationbase.jar"))
    implementation(files("libs/edgeplatform.jar"))
    implementation(files("libs/aibase.jar"))
}

tasks.test {

}
