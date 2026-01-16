plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.github.javaparser:javaparser-core:3.25.10")
    implementation(project(":version"))
}

tasks.test {
    useJUnitPlatform()
}