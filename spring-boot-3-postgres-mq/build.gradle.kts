plugins {
    id("io.github.aveleiv.convention.java-spring-boot-3")
}

group = "io.github.aveleiv"
version = "unspecified"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-activemq")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.testcontainers:activemq")
    testImplementation("org.testcontainers:postgresql")
}

