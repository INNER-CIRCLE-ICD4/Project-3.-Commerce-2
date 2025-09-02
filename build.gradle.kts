plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    val projectGroup: String by project
    group = projectGroup

    // if u needs to change the subproject version then override it in each subproject
    version = "2.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    // dependency implementation should be added to subprojects
    dependencies {
        implementation ("ch.qos.logback:logback-classic")
        implementation("org.projectlombok:lombok")

        annotationProcessor("org.projectlombok:lombok")

        testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
        testCompileOnly ("org.projectlombok:lombok")
        testAnnotationProcessor ("org.projectlombok:lombok")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project("common") { tasks.configureEach { enabled = false } }
project("service") { tasks.configureEach { enabled = false } }

