rootProject.name = "commerce"

include(
    ":common:data-serializer",
    ":service:hexagonal",
    ":service:layered",
    ":service:product",
)

// configurations
pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}