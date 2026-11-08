plugins {
    java
    `java-library`
    id("com.gradleup.shadow") version "9.6.1"
    id("maven-publish")
    signing
}

allprojects {
    group = "io.github.ian171"
    version = "0.0.1.2-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.ian171"
            artifactId = "foundry"
            version = project.version.toString()
            pom{
                name.set("Foundry")
                description.set("Minecraft Technology Framework")
                url.set("https://github.com/ian171/Foundry")

                developers {
                    developer {
                        id.set("chen")
                        name.set("Chen")
                        email.set("chenyeguo666888@hotmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("The GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ian171/Foundry.git")
                    developerConnection.set("scm:git:ssh://git@github.com:ian171/Foundry.git")
                    url.set("https://github.com/ian171/Foundry")
                }
            }
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ian171/Foundry")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.gradleProperty("gpr.token").orNull
                    ?: providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
}
signing {
    // 只有在发布到 MavenCentral 仓库时才强制签名
    val isPublishToMavenCentral = gradle.startParameter.taskNames.any { it.contains("MavenCentral") }
    setRequired({ isPublishToMavenCentral })

    // 显式从 properties 中读取并配置
    val keyId = providers.gradleProperty("signing.keyId").orNull
    val password = providers.gradleProperty("signing.password").orNull
    val secretKey = providers.gradleProperty("signing.secretKey").orNull

    if (keyId != null && password != null && secretKey != null) {
        useInMemoryPgpKeys(keyId, secretKey, password)
    }

    // 只有在需要签名时才签署
    if (isPublishToMavenCentral) {
        sign(publishing.publications["maven"])
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
