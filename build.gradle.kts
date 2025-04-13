import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import java.time.LocalDateTime

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20" // Use latest stable Kotlin version
    id("org.jetbrains.intellij.platform") version "2.5.0" // latest intellij-gradle-plugin https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
}

group = "com.ltu"
version = "0.1.0" // This value will be overridden by the CI/CD pipeline using gradle.properties

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.1")

    // Define IntelliJ Platform dependency using the new approach
    intellijPlatform {
        create("IC", "2024.3") // Target IDE - IC for Community Edition, IU for Ultimate Edition, with version
    }
}

// Configure IntelliJ Platform Plugin
intellijPlatform {
    // Plugin configuration
    pluginConfiguration {
        name.set("LTU progress bar")
        vendor.name.set("LTU")
        version.set(project.version.toString())
        ideaVersion {
            sinceBuild.set("233") // Min supported ver.
            untilBuild.set("243.*") // Max supported ver.
        }
    }

    // Plugin verification configuration
    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2023.3")
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2024.3")

            ide(IntelliJPlatformType.IntellijIdeaUltimate, "2023.3")
            ide(IntelliJPlatformType.IntellijIdeaUltimate, "2024.3")
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // Extract version from gradle.properties for CI/CD pipeline
    val projectVersion = project.findProperty("pluginVersion") as? String ?: project.version.toString()
    project.version = projectVersion

    // Create a task that generates the plugin-info.properties file
    val generatePluginInfo by registering {
        doLast {
            val pluginInfoDir = file("${project.layout.buildDirectory}/resources/main/META-INF")
            pluginInfoDir.mkdirs()

            val pluginInfoFile = file("${pluginInfoDir}/plugin-info.properties")
            pluginInfoFile.writeText(
                """
                # Auto-generated file - DO NOT EDIT MANUALLY
                # Generated on ${LocalDateTime.now()}
                version=$projectVersion
                name=LTU progress bar
                vendor=LTU
            """.trimIndent()
            )
        }
    }

    // Make sure the plugin info is generated before processResources
    processResources {
        dependsOn(generatePluginInfo)
    }

    buildPlugin {
        project.version = projectVersion
    }

    publishPlugin {
        token.set(System.getenv("JETBRAINS_MARKETPLACE_TOKEN"))
    }

    test {
        useJUnitPlatform()
    }
}
