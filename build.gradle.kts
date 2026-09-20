plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
}

group = "com.starshootercity"
version = "2.10.10-cozy.3"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

allprojects {
    tasks.withType<ProcessResources> {
        inputs.property("version", rootProject.version)
        filesMatching("**plugin.yml") {
            expand("version" to rootProject.version)
        }
        filesMatching("**extension.yml") {
            expand("version" to rootProject.version)
        }
    }
}

dependencies {
    // Plugin code
    implementation(project(":core"))
    implementation(project(":version"))
    implementation(project(":folia"))
    implementation(project(":1.21.11"))

    // Dependencies
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    implementation("org.json:json:20250517")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("org.eclipse.jetty:jetty-server:11.0.7")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.7")
    implementation("org.eclipse.jetty:jetty-webapp:11.0.7")
    implementation("dev.triumphteam:triumph-gui-paper:3.1.13")
}

tasks.shadowJar {
    relocate("dev.triumphteam.gui", "com.starshootercity.libraries.triumph.gui")
}

tasks {
    compileJava {
        options.release.set(17)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}
