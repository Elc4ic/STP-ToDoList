plugins {
    id("application")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "dev.stp"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("dev.stp.MainKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.getByName<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "dev.stp.MainKt"
    }
}

dependencies {
    implementation(libs.serialization.kotlinx.json)
    implementation(libs.server.auth)
    implementation(libs.server.auth.jwt)
    implementation(libs.server.callLogging)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.server.cio)
    implementation(libs.server.config.yaml)
    implementation(libs.server.contentNegotiation)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.server.core)
    implementation(libs.server.statusPages)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.h2database.h2)
    implementation(libs.h2database.r2dbc)
    implementation(libs.insert.koin.koinKtor)
    implementation(libs.insert.koin.koinLoggerSlf4j)
    implementation(libs.logback.classic)
    implementation(libs.postgresql)
    implementation(libs.jbcrypt)
    implementation(libs.hikaricp)
    implementation(libs.dotenv.kotlin)
    implementation(project(":shared"))

    testImplementation(kotlin("test"))
    testImplementation(libs.server.testHost)
}
