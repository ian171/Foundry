plugins {
    java
    `java-library`
    id("com.gradleup.shadow") version "9.6.1"
}

allprojects {
    group = "com.github.ian171"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

// Root project configuration - Main plugin JAR
dependencies {
    // Include all core Foundry modules
    implementation(project(":foundry-api"))
    implementation(project(":foundry-core"))
    implementation(project(":foundry-common"))
    implementation(project(":foundry-energy"))
    implementation(project(":foundry-fluid"))
    implementation(project(":foundry-block"))
    implementation(project(":foundry-machine"))
    implementation(project(":foundry-network"))
    implementation(project(":foundry-gui"))
    implementation(project(":foundry-entity"))
    implementation(project(":foundry-recipe"))
    implementation(project(":foundry-storage"))
    implementation(project(":foundry-platform:platform-bukkit"))
    implementation(project(":foundry-platform:platform-paper"))
    implementation(project(":foundry-folia"))

    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("Foundry-${project.version}.jar")

        // Relocate dependencies to avoid conflicts
        relocate("org.sqlite", "cn.epicmc.foundry.libs.sqlite")

        // Exclude unnecessary files
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        // Process resources
        from(sourceSets.main.get().output)

        // Include all foundry modules
        configurations = listOf(project.configurations.runtimeClasspath.get())

        manifest {
            attributes(
                "Implementation-Title" to "Foundry",
                "Implementation-Version" to project.version,
                "Multi-Release" to "true"
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }

    named<Copy>("processResources") {
        val props = mapOf(
            "version" to project.version,
            "description" to "Minecraft Technology Framework"
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    dependencies {
        // Common test dependencies
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
