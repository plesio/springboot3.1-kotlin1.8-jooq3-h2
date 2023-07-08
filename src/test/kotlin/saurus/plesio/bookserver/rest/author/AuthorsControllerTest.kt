package saurus.plesio.bookserver.rest.author

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.openapi.generated.model.AuthorsResponse
import saurus.plesio.bookserver.util.customObjectMapper
import saurus.plesio.bookserver.util.generateAuthorName
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec


@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class AuthorsControllerTest(
  private val mockMvc: MockMvc,
  private val authorRepository: AuthorRepository,
  private val bookRepository: BookRepository
) : FunSpec({
  beforeSpec {
    migrateFlywayOnBeforeSpec(container)
    authorRepository.deleteAll()
  }

  afterTest { authorRepository.deleteAll() }

  test("simple get and check empty.") {
    mockMvc.get("/api/v1/authors")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString shouldBe """{"authors":[]}"""
  }

  val test2 = "find from 1000 records and check response."
  test(test2) {

    val authorsNames1000 = (1..999).map { generateAuthorName() }.plus("山田 光三郎")
    authorsNames1000.map { authorName ->
      authorRepository.insert(authorName, (1960..2011).random(), "")
    }
    val targetFirstNameSize = authorsNames1000.filter { it.contains("山田") }.size

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors?authorName=山田")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString
    val after = System.currentTimeMillis()
    // jackson で List<Author> に parse
    val authorsResponse = customObjectMapper.readValue(resultStr, AuthorsResponse::class.java)
    logger.info("TEST-TEST:${test2}: response-time=${after - before} ms, target=山田, response-size=${authorsResponse.authors.size} , db-target-count=$targetFirstNameSize")
    authorsResponse.authors.size shouldBe targetFirstNameSize
  }

  val test3 = "find from 10000 records and check response."
  test(test3) {

    val authorsNames10000 = (1..9999).map { generateAuthorName() }.plus("山田 光三郎")
    authorsNames10000.map { authorName ->
      authorRepository.insert(authorName, (1960..2011).random(), "")
    }
    val targetFirstNameSize = authorsNames10000.filter { it.contains("山田") }.size

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors?authorName=山田")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString
    val after = System.currentTimeMillis()
    // jackson で List<Author> に parse
    val authorsResponse = customObjectMapper.readValue(resultStr, AuthorsResponse::class.java)
    logger.info("TEST-TEST::${test2}: response-time=${after - before} ms, target=山田, response-size=${authorsResponse.authors.size} , db-target-count=$targetFirstNameSize")
    authorsResponse.authors.size shouldBe targetFirstNameSize
  }


}) {
  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthorsControllerTest::class.java)!!

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