package saurus.plesio.bookserver.util

import org.flywaydb.core.Flyway
import org.testcontainers.containers.MariaDBContainer
import saurus.plesio.bookserver.rest.author.AuthorsControllerTest

fun initTestContainer(): MariaDBContainer<out MariaDBContainer<*>> {

  val container = MariaDBContainer("mariadb:10.11.4-jammy").apply {
    withDatabaseName("bookdb")
    withExposedPorts(3306)
    withUsername("maria")
    withPassword("mariaMaria")
    start()
    // -- ここで、Spring Bootの設定を上書きする(しないと開発設定上のDBに接続しに行く)
    System.setProperty("spring.datasource.url", jdbcUrl)
    System.setProperty("spring.datasource.username", username)
    System.setProperty("spring.datasource.password", password)
  }
  return container
}

fun migrateFlywayOnBeforeSpec(container:MariaDBContainer<out MariaDBContainer<*>>){
  //起動したDBコンテナでマイグレーションを実行する
  Flyway.configure()
    .dataSource(AuthorsControllerTest.container.jdbcUrl, AuthorsControllerTest.container.username, AuthorsControllerTest.container.password)
    .load()
    .migrate()
}