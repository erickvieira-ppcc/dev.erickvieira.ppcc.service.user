rootProject.name = "user-service"

pluginManagement {
    val openApiGeneratorVersion: String by settings
    val dependencyManagementVersion: String by settings
    val springBootVersion: String by settings
    plugins {
        id("org.openapi.generator") version openApiGeneratorVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
    }
}