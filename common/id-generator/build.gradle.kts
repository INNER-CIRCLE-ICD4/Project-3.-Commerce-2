dependencies {
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-core")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")

    compileOnly("org.springframework.boot:spring-boot-starter")

}


