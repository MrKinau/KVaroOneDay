// More about the setup here: https://github.com/DevSrSouza/KotlinBukkitAPI/wiki/Getting-Started
plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "systems.kinau"
version = "1.0"

repositories {
    jcenter()
    mavenLocal()

    // minecraft
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    //kotlinbukkitapi with backup repo
    maven("http://nexus.devsrsouza.com.br/repository/maven-public/")
    
    //plugins
    
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    //minecraft
    compileOnly("org.spigotmc:spigot:1.16.4-R0.1-SNAPSHOT")

    //kotlinbukkitapi
    val changing = Action<ExternalModuleDependency> { isChanging = true }
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:core:0.2.0-SNAPSHOT", changing)
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:serialization:0.2.0-SNAPSHOT", changing)
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:plugins:0.2.0-SNAPSHOT", changing)
    compileOnly("br.com.devsrsouza.kotlinbukkitapi:exposed:0.2.0-SNAPSHOT", changing)

    //plugins
    val transitive = Action<ExternalModuleDependency> { isTransitive = false }
    implementation("club.minnced:discord-webhooks:0.4.1")
    
}

bukkit {
    main = "systems.kinau.kvaro.KVaroPlugin"
    depend = listOf("KotlinBukkitAPI")
    description = "A simple Varo plugin written in Kotlin"
    author = "MrKinau"
    website = "https://david.ldtke.de"
    apiVersion = "1.16"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi"
    }
    shadowJar {
        dependencies {
            exclude(dependency("org.jetbrains:annotations:.*"))
        }
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(120, "minutes")
}