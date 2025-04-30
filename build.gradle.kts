import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "2.1.20" // Use latest stable Kotlin version
    id("org.jetbrains.intellij.platform") version "2.5.0" // latest intellij-gradle-plugin https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
}

group = "com.ltu.progressbar"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    intellijPlatform {
        intellijIdeaUltimate("2024.3")

        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "LTU Progress Bar"
        ideaVersion {
            sinceBuild = "243" // Min supported ver.
            untilBuild = provider { null }// Max supported ver.
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    publishing {
        hidden = true
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
            apiVersion = KotlinVersion.KOTLIN_2_1
            languageVersion = KotlinVersion.KOTLIN_2_1
        }
    }

    signPlugin {
        // comment / change these when building your environment
        certificateChainFile.set(File(System.getenv("LTU_PBP_CERTIFICATE_CHAIN_FILE")))
        privateKeyFile = File(System.getenv("LTU_PBP_PRIVATE_KEY_FILE"))
        password = System.getenv("LTU_PBP_PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        token = System.getenv("LTU_PBP_PUBLISH_TOKEN")
    }
}
