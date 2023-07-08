package saurus.plesio.bookserver.rest.author

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.jooq.tables.pojos.Author
import saurus.plesio.bookserver.util.customObjectMapper
import saurus.plesio.bookserver.util.generateAuthorName
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec


@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class AuthorControllerTest(
  private val mockMvc: MockMvc,
  private val authorRepository: AuthorRepository
) : FunSpec({
  beforeSpec {
    migrateFlywayOnBeforeSpec(container)
    authorRepository.deleteAll()
  }

  afterTest { authorRepository.deleteAll() }

  test("simple 1 post and check") {
    mockMvc.post("/api/v1/authors") {
      contentType = MediaType.APPLICATION_JSON
      content = """{"authorName": "${generateAuthorName()}","birthYear": "${(1960..2011).random()}","remarks": ""}"""
    }.andExpect {
      status { isOk() }
    }
    authorRepository.listAll().size shouldBe 1
  }

  val test1 = "simple 1 get and check"
  test(test1) {
    val authorName = generateAuthorName()
    val authorBirthYear = (1960..2011).random()
    val authorId = authorRepository.insert(authorName, authorBirthYear, "")

    val retJsonTxt = mockMvc.get("/api/v1/authors/$authorId") {
      contentType = MediaType.APPLICATION_JSON
    }
      .andExpect {
        status { isOk() }
      }.andReturn()
      .response
      .contentAsString

    retJsonTxt.isBlank() shouldNotBe true
    retJsonTxt shouldNotBe "{}"
    val retAuthor = customObjectMapper.readValue(retJsonTxt, Author::class.java)
    logger.info("TEST-TEST:${test1} retAuthor:${retAuthor}")
    (retAuthor.authorName == authorName) shouldBe true
  }

  test("get not exist author.") {
    mockMvc.get("/api/v1/authors/dummy")
      .andExpect {
        status { isNotFound() }
      }
  }

  test("patch 1 user and check") {
    val authorName = generateAuthorName()
    val authorBirthYear = (1960..2011).random()
    val authorId = authorRepository.insert(authorName, authorBirthYear, "")

    val newAuthorName = generateAuthorName()
    val newAuthor = Author(authorId, newAuthorName, authorBirthYear, "")
    mockMvc.patch("/api/v1/authors/$authorId") {
      contentType = MediaType.APPLICATION_JSON
      content = customObjectMapper.writeValueAsString(newAuthor.copy())
    }.andExpect {
      status { isOk() }
    }

    val dbAuthor = authorRepository.findById(authorId)
    dbAuthor?.authorName shouldNotBe authorName
    dbAuthor?.authorName shouldBe newAuthorName
  }


}) {
  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthorControllerTest::class.java)!!

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