package saurus.plesio.bookserver.db

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec

@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class AuthorRepositoryTest(
  private val authorRepository: AuthorRepository,
  private val bookRepository: BookRepository
) : FunSpec({
  beforeSpec {
    migrateFlywayOnBeforeSpec(container)
    bookRepository.deleteAll()
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
    val logger: Logger = LoggerFactory.getLogger(AuthorRepositoryTest::class.java)!!

    @Container
    val container = initTestContainer()

    init {
      container.start()
    }

    @JvmStatic
    @DynamicPropertySource
    fun sqlProperties(registry: DynamicPropertyRegistry) {
      // -- ここで、Spring Bootの設定を上書きする(しないと開発設定上のDBに接続しに行く)
      registry.add("db.url") { container.jdbcUrl }
      registry.add("db.user") { container.username }
      registry.add("db.password") { container.password }
      registry.add("spring.datasource.url") { container.jdbcUrl }
      registry.add("spring.datasource.username") { container.username }
      registry.add("spring.datasource.password") { container.password }
    }
  }
}