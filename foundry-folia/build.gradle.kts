dependencies {
    api(project(":foundry-api"))
    implementation(project(":foundry-core"))
    implementation(project(":foundry-common"))
    implementation(project(":foundry-platform:platform-paper"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}
