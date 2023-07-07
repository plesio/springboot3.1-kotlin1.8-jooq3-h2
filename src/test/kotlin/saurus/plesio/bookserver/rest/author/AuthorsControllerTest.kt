package saurus.plesio.bookserver.rest.author

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.model.AuthorsResponse
import saurus.plesio.bookserver.util.generateAuthorName
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec


@Suppress("unused")
@SpringBootTest
@AutoConfigureMockMvc
internal class AuthorsControllerTest(
  private val mockMvc: MockMvc,
  private val authorRepository: AuthorRepository
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

    val before = System.currentTimeMillis();
    val resultStr = mockMvc.get("/api/v1/authors?authorName=山田")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString
    val after = System.currentTimeMillis();
    // jackson で List<Author> に parse
    val authorsResponse = jacksonObjectMapper().readValue(resultStr, AuthorsResponse::class.java)
    logger.info("${test2}: response-time=${after-before} ms, target=山田, response-size=${authorsResponse.authors.size} , db-target-count=$targetFirstNameSize")
    authorsResponse.authors.size shouldBe targetFirstNameSize
  }

  val test3 = "find from 10000 records and check response."
  test(test2) {

    val authorsNames10000 = (1..9999).map { generateAuthorName() }.plus("山田 光三郎")
    authorsNames10000.map { authorName ->
      authorRepository.insert(authorName, (1960..2011).random(), "")
    }
    val targetFirstNameSize = authorsNames10000.filter { it.contains("山田") }.size

    val before = System.currentTimeMillis();
    val resultStr = mockMvc.get("/api/v1/authors?authorName=山田")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString
    val after = System.currentTimeMillis();
    // jackson で List<Author> に parse
    val authorsResponse = jacksonObjectMapper().readValue(resultStr, AuthorsResponse::class.java)
    logger.info("${test2}: response-time=${after-before} ms, target=山田, response-size=${authorsResponse.authors.size} , db-target-count=$targetFirstNameSize")
    authorsResponse.authors.size shouldBe targetFirstNameSize
  }


}) {
  companion object {
    val container = initTestContainer()
    val logger: Logger = LoggerFactory.getLogger(AuthorsControllerTest::class.java)!!
  }
}