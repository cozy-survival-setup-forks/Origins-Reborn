plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.opencollab.dev/main/") }
    maven { url = uri("https://repo.viaversion.com") }
    maven { url = uri("https://maven.enginehub.org/repo/") }

    maven("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc"
    }
}

dependencies {
    // Annotations
    implementation("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Other plugins
    compileOnly("com.viaversion:viaversion-api:5.0.0")
    compileOnly("org.geysermc.geyser:api:2.7.0-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
    compileOnly("com.github.authme:authmereloaded:5.6.0-beta2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6") // Earliest with 1.18 support.
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.20") // Earliest with 1.18 support.
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.6.3")
    compileOnly(files("libs/gsit.jar"))
    compileOnly("com.github.SkriptLang:Skript:2.9.1")

    // PAPI
    compileOnly("me.clip:placeholderapi:2.11.5")

    // Server software
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    // Plugin code
    compileOnly(project(":version"))
    compileOnly(project(":folia"))
    compileOnly(project(":1.21.11"))

    compileOnly("org.json:json:20250517")
    compileOnly("net.objecthunter:exp4j:0.4.8")
    compileOnly("org.eclipse.jetty:jetty-server:11.0.7")
    compileOnly("org.eclipse.jetty:jetty-servlet:11.0.7")
    compileOnly("org.eclipse.jetty:jetty-webapp:11.0.7")
    implementation("dev.triumphteam:triumph-gui-paper:3.1.13")
}

tasks {
    compileJava {
        options.release.set(17)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    disableAutoTargetJvm()
}

tasks.test {
    useJUnitPlatform()
}
