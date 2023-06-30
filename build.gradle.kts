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

buildscript {
  dependencies {
    classpath("org.flywaydb:flyway-mysql:9.8.1")
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")


  // -- TEST
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  // -- DB
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
  runtimeOnly("com.mysql:mysql-connector-j:8.0.33") // for flyway

  // -- jOOQ
  implementation("org.springframework.boot:spring-boot-starter-jooq") {
    exclude(group = "org.jooq", module = "jooq")
  }
  implementation("org.jooq:jooq:3.18.5")

  jooqGenerator("com.mysql:mysql-connector-j:8.0.33")
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

// docker-compose にも直書きしてあるので、こっちも直書きにする
// -- （本来は環境変数など非Git管理領域から取得する）
val dbUrlAsMySQL = "jdbc:mysql://localhost:3306/bookdb"
val dbUserName = "maria"
val dbPasswd = "mariaMaria"

flyway {
  url = dbUrlAsMySQL
  user = dbUserName
  password = dbPasswd
  cleanDisabled = false
  baselineVersion = "0.0.0"
  baselineOnMigrate = true
}

jooq {
  configurations {
    create("main") {
      jooqConfiguration.apply {
        jdbc.apply {
          url = dbUrlAsMySQL
          user = dbUserName
          password = dbPasswd
        }
        generator.apply {
          name = "org.jooq.codegen.KotlinGenerator"
          database.apply {
            name = "org.jooq.meta.mysql.MySQLDatabase"
            inputSchema = "bookdb"
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