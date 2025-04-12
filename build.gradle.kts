plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22" // Use latest stable Kotlin version
    id("org.jetbrains.intellij") version "1.17.2" // Use latest intellij-gradle-plugin
}

group = "com.ltu"
version = "0.1.0" // This value will be overridden by the CI/CD pipeline using gradle.properties

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3") // Target IntelliJ IDEA version
    type.set("IC") // Target IDE - IC for Community Edition, IU for Ultimate Edition
    
    plugins.set(listOf(/* Add plugin dependencies here, e.g., "java" */))
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    
    patchPluginXml {
        sinceBuild.set("233") // Minimum supported version
        untilBuild.set("243.*") // Maximum supported version
        // Plugin description is loaded from plugin.xml
    }
    
    // Extract version from gradle.properties for CI/CD pipeline
    val projectVersion = project.findProperty("pluginVersion") as? String ?: project.version.toString()
    project.version = projectVersion
    
    // Create a task that generates the plugin-info.properties file
    val generatePluginInfo by registering {
        doLast {
            val pluginInfoDir = file("${project.buildDir}/resources/main/META-INF")
            pluginInfoDir.mkdirs()
            
            val pluginInfoFile = file("${pluginInfoDir}/plugin-info.properties")
            pluginInfoFile.writeText("""
                # Auto-generated file - DO NOT EDIT MANUALLY
                # Generated on ${java.time.LocalDateTime.now()}
                version=$projectVersion
                name=LTU Progress Bar
                vendor=LTU
            """.trimIndent())
        }
    }
    
    // Make sure the plugin info is generated before processResources
    processResources {
        dependsOn(generatePluginInfo)
    }
    
    prepareSandbox {
        project.version = projectVersion
    }
    
    publishPlugin {
        token.set(System.getenv("JETBRAINS_MARKETPLACE_TOKEN"))
    }
    
    runPluginVerifier {
        ideVersions.set(listOf("2023.3", "2024.1"))
    }
    
    test {
        useJUnitPlatform()
    }
}
