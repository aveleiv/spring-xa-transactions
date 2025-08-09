package io.github.aveleiv.convention

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation(libs.findLibrary("atomikosSpringBoot3").get())
    implementation("jakarta.transaction:jakarta.transaction-api")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation(libs.findLibrary("assertjDB").get())
}