plugins {
    id("java")
}

group = "org.thingai.meo.common"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly(files("../libs/applicationbase.jar"))
    compileOnly("com.google.code.gson:gson:2.13.2")
}

tasks.test {
    useJUnitPlatform()
}