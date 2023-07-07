package saurus.plesio.bookserver.rest.author

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import saurus.plesio.bookserver.db.AuthorRepository
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
    mockMvc.post("/api/v1/authors"){
      contentType = MediaType.APPLICATION_JSON
      content = """{"authorName": "${generateAuthorName()}","birthYear": "${(1960..2011).random()}","remarks": ""}"""
    }
    authorRepository.listAll().size shouldBe 1
  }


}) {
  companion object {
    val container = initTestContainer()
  }
}