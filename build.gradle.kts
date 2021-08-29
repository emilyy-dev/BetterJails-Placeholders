plugins {
    java
    id("com.github.hierynomus.license-base") version "0.16.1"
}

project.group = "io.github.emilyy-dev"
project.version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

license {
    header = file("header.txt")
    encoding = Charsets.UTF_8.name()
    mapping("java", "DOUBLESLASH_STYLE")
    include("**/*.java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.8.8-R0.1-SNAPSHOT") {
        exclude("net.md-5", "bungeecord-chat")
    }
    compileOnly("io.github.emilyy-dev", "betterjails-api", "1.5-SNAPSHOT")
    compileOnly("me.clip", "placeholderapi", "2.10.10")

    compileOnly("org.jetbrains", "annotations", "21.0.1")
}

tasks {
    check {
        finalizedBy(licenseMain)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}
