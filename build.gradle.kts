import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.compose") version "1.6.0"
}

group = "es.iesra.ctfm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
    // DCS: Base de datos H2
    implementation("com.h2database:h2:2.2.224")
    // DCS: HikariCP
    implementation("com.zaxxer:HikariCP:5.0.0")
    // DCS: Arregla el warning SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
    implementation("org.slf4j:slf4j-nop:2.0.6")
    // Bibliotecas para manejar JSON
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    // Bibliotecas para manejar XML (JAXB)
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    // Dependencias Kotest para el testeo
    testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
    testImplementation("io.kotest:kotest-assertions-core:5.4.2")
    testImplementation("io.kotest:kotest-property:5.4.2")
    // Dependencia para MockK
    testImplementation("io.mockk:mockk:1.13.11")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "un9pe"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "19"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "19"
    targetCompatibility = "19"
}

tasks.register<Jar>("createJar") {
    dependsOn("build")
    archiveFileName.set("un9pe.jar")
    from(sourceSets.main.get().output)
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}
