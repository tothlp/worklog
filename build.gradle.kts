plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "hu.tothlp"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "hu.tothlp.worklog.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                dependencies {
                    implementation("com.github.ajalt.clikt:clikt:4.4.0")
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
                    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                    implementation("com.squareup.okio:okio:3.9.0")


                }
            }
        }

        val nativeTest by getting
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.BIN
}