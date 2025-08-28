dependencies {
    // Common modules
    implementation(project(":common:event-contracts"))
    implementation(project(":common:id-generator"))
    
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Database drivers
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")  // MySQL for dev/prod

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("com.h2database:h2")
}

// Mockito inline mock maker를 위한 설정
tasks.test {
    jvmArgs("-javaagent:${configurations.testRuntimeClasspath.get().files.find { it.name.startsWith("byte-buddy-agent") }}")
}
