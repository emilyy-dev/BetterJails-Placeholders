plugins {
    java
    id("com.github.hierynomus.license-base") version "0.16.1"
}

project.group = "io.github.emilyy-dev"
project.version = "1.0.0"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(8)
  }
}

license {
    header = file("header.txt")
    encoding = "UTF-8"
    mapping("java", "DOUBLESLASH_STYLE")
    include("**/*.java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        exclude("net.md-5", "bungeecord-chat")
    }
    compileOnly("io.github.emilyy-dev:betterjails-api:1.5")
    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("org.jetbrains:annotations:26.0.1")
}

tasks {
    check {
        dependsOn(licenseMain)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}
