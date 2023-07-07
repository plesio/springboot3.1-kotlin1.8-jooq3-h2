package saurus.plesio.bookserver.rest.author

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.jooq.tables.pojos.Author
import saurus.plesio.bookserver.util.generateAuthorName
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec


@Suppress("unused")
@SpringBootTest
@AutoConfigureMockMvc
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

  test("simple 1 get and check") {
    val authorName = generateAuthorName()
    val authorBirthYear = (1960..2011).random()
    val authorId = authorRepository.insert(authorName, authorBirthYear, "")

    val retJsonTxt = mockMvc.get("/api/v1/authors/$authorId"){
      contentType = MediaType.APPLICATION_JSON
    }
      .andExpect {
        status { isOk() }
      }.andReturn()
      .response
      .contentAsString

    retJsonTxt.isBlank() shouldNotBe true
    retJsonTxt shouldNotBe "{}"
    val retAuthor = jacksonObjectMapper().readValue(retJsonTxt, Author::class.java)
    (retAuthor.authorName == authorName) shouldBe true
  }

  test("get from empty db.") {
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
      content = jacksonObjectMapper().writeValueAsString(newAuthor.copy())
    }.andExpect {
      status { isOk() }
    }

    val dbAuthor = authorRepository.findById(authorId)
    // logger.info(dbAuthor.toString() + ", " + newAuthor.toString())
    (dbAuthor?.authorName == authorName) shouldBe false
    (dbAuthor?.authorName == newAuthorName) shouldBe true
  }


}) {
  companion object {
    val container = initTestContainer()
    val logger: Logger = LoggerFactory.getLogger(AuthorControllerTest::class.java)!!
  }
}