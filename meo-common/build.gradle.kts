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

    implementation(files("../libs/applicationbase.jar"))
}

tasks.test {
    useJUnitPlatform()
}