import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "3.1.1"
  id("io.spring.dependency-management") version "1.1.0"
  id("nu.studer.jooq") version "8.2.1"
  id("org.flywaydb.flyway") version "9.20.0"
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
}

group = "saurus.plesio"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-jooq")
  implementation("org.jooq:jooq:3.18.5")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  jooqGenerator("com.h2database:h2")
  jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

val h2FileDir = "${rootDir.absoluteFile}/h2bookdb"
val h2Url = "jdbc:h2:file:${h2FileDir}"

flyway {
  driver = "org.h2.Driver"
  url = h2Url
  user = "sa"
  password = "password"
}

jooq {
  configurations {
    create("main") {
      jooqConfiguration.apply {
        jdbc.apply {
          url = h2Url
          user = "sa"
          password = "password"
        }
        generator.apply {
          name = "org.jooq.codegen.KotlinGenerator"
          database.apply {
            name = "org.jooq.meta.h2.H2Database"
            inputSchema = "PUBLIC"
          }
          generate.apply {
            isDeprecated = false
            isTables = true
          }
          target.apply {
            packageName = "saurus.plesio.bookserver"
            directory = "${buildDir}/generated/source/jooq/main"
          }
        }
      }
    }
  }
}