/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    java
    `maven-publish`
    checkstyle
    jacoco
    id("org.springframework.boot") version "2.6.1"
    id("com.brtvsk.tests-manager") version "1.0"
    id("org.sonarqube") version "3.3"
}

repositories {
    mavenCentral()
    maven {
        name = "MyPrivateGCPRepo"
        url = uri("https://original-glyph-349716.lm.r.appspot.com/")
    }
}

dependencies {
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")
    annotationProcessor("org.immutables:value:2.8.8")

    implementation("org.springframework.boot:spring-boot-starter:2.6.1")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.6.1")
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.1")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.2")
    implementation("org.springframework.boot:spring-boot-starter-security:2.6.1")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:5.5.1")
    implementation("org.springframework.security:spring-security-oauth2-jose:5.6.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.1")
    testImplementation("org.testcontainers:junit-jupiter:1.16.2")
    testImplementation("org.testcontainers:mongodb:1.16.2")
    testImplementation("com.github.dasniko:testcontainers-keycloak:1.9.0")
    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("org.springframework.security:spring-security-test:5.5.1")
    compileOnly("org.immutables:value:2.8.8")

    // My Packages
    implementation("com.brtvsk:tests-manager-annotations:1.0-SNAPSHOT")
}

group = "com.brtvsk"
version = "1.0-SNAPSHOT"
description = "todo-service"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.test {
    ignoreFailures = true
    val integrationTest = System.getProperty("integrationTest")?.toBoolean() ?: false
    useJUnitPlatform {
        if (integrationTest) includeTags("integration") else excludeTags("integration")
    }
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    configure<JacocoTaskExtension> {
        isEnabled = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
        xml.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/xml/jacocoTestReport.xml").get().asFile)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).matching {
                exclude(
                    "com/brtvsk/todoservice/config/**/*",
                    "com/brtvsk/todoservice/model/**/*",
                    "**/*Config.*",
                    "**/TodoServiceApplication.*"
                )
            }
        })
    )
}

sonarqube {
    properties {
        property("sonar.organization", "brutovsky")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectKey", "brutovsky_micro2do")
        property("sonar.exclusions", "**/*Config.*, **/*ServiceApplication.*")
        property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.dir("reports/jacoco/xml/jacocoTestReport.xml").get().asFile.path)
    }
}

testsManagerConfig {
    enabled = true
    tag("85%")
    tag("fast", "50%")
}