dependencies {
    api(project(":foundry-api"))
    implementation(project(":foundry-core"))
    implementation(project(":foundry-common"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    // SQLite database
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
}
