package saurus.plesio.bookserver.rest.db

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.MariaDBContainer
import saurus.plesio.bookserver.db.AuthorRepository

@Suppress("unused")
@SpringBootTest
internal class AuthorRepositoryTest(
  private val authorRepository: AuthorRepository
) : FunSpec({
  beforeSpec {
    //起動したDBコンテナでマイグレーションを実行する
    Flyway.configure()
      .dataSource(container.jdbcUrl, container.username, container.password)
      .load()
      .migrate()
    authorRepository.deleteAll()
  }

  afterTest { authorRepository.deleteAll() }

  test("simple save and find") {
    val authorId = authorRepository.insert("first", 1992, "last")
    val find = authorRepository.findById(authorId)
    authorId shouldBe find?.authorId
  }

  test("simple find count one") {
    authorRepository.insert("first taro", 1992, "")
    authorRepository.listAll().size shouldBe 1
  }

}) {
  companion object {
    //MySQLコンテナを起動
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

  }
}