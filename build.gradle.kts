plugins {
    java
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.wgdetective"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val snippetsDir by extra { file("build/generated-snippets") }
extra["mapstructVersion"] = "1.5.2.Final"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    //implementation("org.liquibase:liquibase-core")
    implementation("org.springframework:spring-jdbc")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    //developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")

    testAnnotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    //testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
}
