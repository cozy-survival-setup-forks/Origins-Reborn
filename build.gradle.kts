plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
}

group = "com.starshootercity"
version = "2.10.9"

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
    implementation(project(":1.18.1", "reobf"))
    implementation(project(":1.18.2", "reobf"))
    implementation(project(":1.19", "reobf"))
    implementation(project(":1.19.1", "reobf"))
    implementation(project(":1.19.2", "reobf"))
    implementation(project(":1.19.3", "reobf"))
    implementation(project(":1.19.4", "reobf"))
    implementation(project(":1.20", "reobf"))
    implementation(project(":1.20.1", "reobf"))
    implementation(project(":1.20.2", "reobf"))
    implementation(project(":1.20.3", "reobf"))
    implementation(project(":1.20.4", "reobf"))
    implementation(project(":1.20.6", "reobf"))
    implementation(project(":1.21", "reobf"))
    implementation(project(":1.21.1", "reobf"))
    implementation(project(":1.21.3", "reobf"))
    implementation(project(":1.21.4", "reobf"))
    implementation(project(":1.21.5"))
    implementation(project(":1.21.6"))
    implementation(project(":1.21.7"))
    implementation(project(":1.21.8"))
    implementation(project(":1.21.9"))

    // Dependencies
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    implementation(files("core/libs/json-20250517.jar"))
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("org.eclipse.jetty:jetty-server:11.0.7")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.7")
    implementation("org.eclipse.jetty:jetty-webapp:11.0.7")
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