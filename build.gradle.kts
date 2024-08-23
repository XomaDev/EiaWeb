plugins {
    id("java")
    kotlin("jvm")
}

group = "space.themelon.eia64"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation(files("libs/Eia64.main.jar"))
    implementation("org.json:json:20240303")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    manifest {
        attributes("Main-Class" to "space.themelon.eiaweb.Main")
    }
    from({
        configurations.compileClasspath.get().filter {
            it.exists()
        }.map {
            if (it.isDirectory) it else project.zipTree(it)
        }
    })
    with(tasks.jar.get())
    duplicatesStrategy = DuplicatesStrategy.WARN
}

kotlin {
    jvmToolchain(11)
}