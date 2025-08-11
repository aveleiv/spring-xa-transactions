plugins {
    id("io.github.aveleiv.convention.java-common")
    alias(libs.plugins.springBoot3)
    alias(libs.plugins.spring6DependencyManagement)
}

group = "io.github.aveleiv"
version = "unspecified"


dependencies {
    implementation(libs.spring.boot.jdbc)
    implementation(libs.spring.boot.activemq)
    implementation(libs.atomikos.springBoot3)
    implementation(libs.jakarta.transactions)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    runtimeOnly(libs.postgres)

    testImplementation(libs.spring.boot.test)
    testImplementation(libs.spring.boot.testcontainers)

    testImplementation(libs.assertj.db)

    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.activemq)
    testImplementation(libs.testcontainers.postgres)
}

