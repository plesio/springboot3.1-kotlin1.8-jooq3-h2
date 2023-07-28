package saurus.plesio.bookserver.rest.book

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
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.model.Book
import saurus.plesio.bookserver.util.*
import java.time.LocalDate


@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class BookControllerTest(
  private val mockMvc: MockMvc,
  private val bookRepository: BookRepository,
  private val authorRepository: AuthorRepository
) : FunSpec({
  beforeSpec {
    migrateFlywayOnBeforeSpec(container)
    bookRepository.deleteAll()
    authorRepository.deleteAll()
  }

  afterTest {
    bookRepository.deleteAll()
    authorRepository.deleteAll()
  }

  test("simple 1 get and check") {
    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
    val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
    val targetBookId = bookRepository.insert(dummyAuthorId, "DUMMY BOOK", null, publishDate, "")

    val retJsonTxt = mockMvc.get("/api/v1/books/$targetBookId") {
      contentType = MediaType.APPLICATION_JSON
    }
      .andExpect {
        status { isOk() }
      }.andReturn()
      .response
      .contentAsString

    retJsonTxt.isBlank() shouldNotBe true
    retJsonTxt shouldNotBe "{}"
    val retBook = customObjectMapper.readValue(retJsonTxt, Book::class.java)
    retBook.bookTitle shouldBe "DUMMY BOOK"
  }

  test("simple 1 patch and check") {
    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
    val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
    val oldBookTitle = generateBookTitle()
    val targetBookId = bookRepository.insert(dummyAuthorId, oldBookTitle, null, publishDate, "")

    val newBookTitle = generateBookTitle()
    val newIsbn = (1..13).map { (0..9).random() }.joinToString("")
    val patchBook = Book(targetBookId, newBookTitle, newIsbn, publishDate, "")
    logger.info("TEST-TEST: patchBook: $patchBook, oldTitle=$oldBookTitle")

    mockMvc.patch("/api/v1/books/$targetBookId") {
      contentType = MediaType.APPLICATION_JSON
      content = customObjectMapper.writeValueAsString(patchBook.copy())
    }
      .andExpect {
        status { isOk() }
      }

    val dbBook = bookRepository.findById(targetBookId)
    dbBook shouldNotBe null
    dbBook?.bookTitle shouldNotBe oldBookTitle
    dbBook?.bookTitle shouldBe newBookTitle
    dbBook?.isbnCode shouldBe newIsbn
  }

}) {
  companion object {
    val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)!!

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