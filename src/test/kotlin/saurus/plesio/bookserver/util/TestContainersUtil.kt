package saurus.plesio.bookserver.util

import org.flywaydb.core.Flyway
import org.testcontainers.containers.MariaDBContainer

fun initTestContainer(): MariaDBContainer<out MariaDBContainer<*>> {

  val container = MariaDBContainer<Nothing>("mariadb:10.11.4-jammy").apply {
    withDatabaseName("bookdb")
    withExposedPorts(3306)
    withUsername("maria")
    withPassword("mariaMaria")
  }
  return container
}

fun migrateFlywayOnBeforeSpec(container: MariaDBContainer<out MariaDBContainer<*>>) {
  //起動したDBコンテナでマイグレーションを実行する
  Flyway.configure()
    .dataSource(
      container.jdbcUrl,
      container.username,
      container.password
    )
    .load()
    .migrate()
}