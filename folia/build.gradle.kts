plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc"
    }
}

dependencies {
    // Annotations
    implementation("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Server software
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")

    // Plugin code
    compileOnly(project(":version"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    disableAutoTargetJvm()
}

tasks {
    compileJava {
        options.release.set(17)
    }
}

tasks.test {
    useJUnitPlatform()
}