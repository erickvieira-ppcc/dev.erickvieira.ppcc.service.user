rootProject.name = "user-service"

pluginManagement {
    val openApiGeneratorVersion: String by settings
    val pitestVersion: String by settings
    val dependencyManagementVersion: String by settings
    val springBootVersion: String by settings
    plugins {
        id("org.openapi.generator") version openApiGeneratorVersion
        id("info.solidsoft.pitest") version pitestVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
    }
}