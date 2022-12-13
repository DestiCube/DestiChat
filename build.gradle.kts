plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.desticube.chat"
version = "1.0-SNAPSHOT"
description = "DestiCube's main chat"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.github.DestiCube:DestiPlaceholders:8933681736")
    compileOnly("net.luckperms:api:5.4")

    implementation("com.github.GamerDuck123:DuckCommons:706d780630")
}

tasks {
    shadowJar {
        minimize()
        archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")

        listOf("com.gamerduck.commons").forEach {
            relocate(it, "${rootProject.group}.commons")
        }
    }

    compileJava {
        options.release.set(17)
//        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}