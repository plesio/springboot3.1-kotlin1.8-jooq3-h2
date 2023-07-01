import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
  id("org.springframework.boot") version "3.1.1"
  id("io.spring.dependency-management") version "1.1.0"
  id("nu.studer.jooq") version "8.2.1"
  id("org.flywaydb.flyway") version "9.20.0"
  id("org.openapi.generator") version "6.6.0"

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
  implementation("org.springframework.boot:spring-boot-starter-validation")
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

  // -- OpenAPI
  compileOnly("io.swagger.core.v3:swagger-annotations:2.2.14")
  compileOnly("io.swagger.core.v3:swagger-models:2.2.14")
  compileOnly("jakarta.validation:jakarta.validation-api")
  compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")

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
      // -- デフォルトで起動時ビルドをしないようにするおまじない。
      val isGenerate = System.getenv("JOOQ_GENERATE")?.toBoolean() ?: false
      generateSchemaSourceOnCompilation.set(isGenerate)
      // --
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
            excludes = "flyway_schema_history"
          }
          generate.apply {
            isDeprecated = false
            isTables = true
            isRecords = true
            isPojos = true
            isDaos = true
          }
          target.apply {
            packageName = "saurus.plesio.bookserver.jooq"
            directory = "${buildDir}/generated/source/jooq/main"
          }
          strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
        }
      }
    }
  }
}

// -- OpenAPI
task<GenerateTask>("generateApiServer") {
  generatorName.set("kotlin-spring")
  inputSpec.set("$projectDir/openapi_v1.yaml")
  outputDir.set("$buildDir/generated/source/openapi/server-code/")
  apiPackage.set("saurus.plesio.bookserver.openapi.generated.controller")
  modelPackage.set("saurus.plesio.bookserver.openapi.generated.model")
  configOptions.set(
    mapOf(
      "interfaceOnly" to "true",
      "useSpringBoot3" to "true",
      "generatedConstructorWithRequiredArgs" to "false"
    )
  )
  // true にすると tags 準拠で、API の interface を生成する
  additionalProperties.set(
    mapOf("useTags" to "true")
  )
}

//Kotlinをコンパイルする前に、generateApiServerタスクを実行
tasks.compileKotlin {
  dependsOn("generateApiServer")
}

kotlin.sourceSets.main {
  kotlin.srcDir("$buildDir/generated/source/openapi/server-code/src/main")
}
