package saurus.plesio.bookserver.rest.author.book

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
import saurus.plesio.bookserver.openapi.generated.model.BooksResponse
import saurus.plesio.bookserver.util.*
import java.time.LocalDate


@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class AuthorBooksControllerTest(
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
    // 消さずに増加していくテストを許可する.
    //    bookRepository.deleteAll()
    //    authorRepository.deleteAll()
  }

  test("simple get and check empty.") {
    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
    mockMvc.get("/api/v1/authors/$dummyAuthorId/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString shouldBe """{"books":[]}"""
  }

  val test2 = "find from + 10*100 book-records and check response."
  test(test2) {
    randomInsertAuthorBooks(9, 100, authorRepository, bookRepository)

    // 対象の著者の周りを作成.
    val dummyAuthorId = authorRepository.insert("山田 光三郎2", 1960, "")

    val bookTitles100 = (1..100).map { generateBookTitle() }
    bookTitles100.map { bookTitle ->
      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
    }

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors/$dummyAuthorId/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString

    val after = System.currentTimeMillis()
    // jackson で Response に parse
    val booksResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
    logger.info("TEST-TEST:${test2}: response-time=${after - before} ms, response-size=${booksResponse.books.size} , db-target-count= may be 100")
    booksResponse.books.size shouldBe 100
  }


  val test3 = "find from + 100 * 100 records and check response."
  test(test3) {
    randomInsertAuthorBooks(99, 100, authorRepository, bookRepository)

    // 対象の著者の周りを作成.
    val dummyAuthorId = authorRepository.insert("山田 光三郎3", 1960, "")

    val bookTitles100 = (1..100).map { generateBookTitle() }
    bookTitles100.map { bookTitle ->
      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
    }

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors/$dummyAuthorId/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString

    val after = System.currentTimeMillis()
    // jackson で Response に parse
    val booksResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
    logger.info("TEST-TEST:${test3}: response-time=${after - before} ms, response-size=${booksResponse.books.size} , db-target-count= may be 100")
    booksResponse.books.size shouldBe 100
  }

  val test4= "find from + 20 * 500 records and check response."
  test(test4) {
    randomInsertAuthorBooks(20, 500, authorRepository, bookRepository)

    // 対象の著者の周りを作成.
    val dummyAuthorId = authorRepository.insert("山田 光三郎4", 1960, "")

    val bookTitles500 = (1..500).map { generateBookTitle() }
    bookTitles500.map { bookTitle ->
      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
    }

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors/$dummyAuthorId/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString

    val after = System.currentTimeMillis()
    // jackson で Response に parse
    val booksResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
    logger.info("TEST-TEST:${test4}: response-time=${after - before} ms, response-size=${booksResponse.books.size} , db-target-count= may be 500")
    booksResponse.books.size shouldBe 500
  }

  val test5= "find from + 3 * 1000 records and check response."
  test(test4) {
    randomInsertAuthorBooks(2, 1000, authorRepository, bookRepository)

    // 対象の著者の周りを作成.
    val dummyAuthorId = authorRepository.insert("山田 光三郎5", 1960, "")

    val bookTitles1000 = (1..1000).map { generateBookTitle() }
    bookTitles1000.map { bookTitle ->
      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
    }

    val before = System.currentTimeMillis()
    val resultStr = mockMvc.get("/api/v1/authors/$dummyAuthorId/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString

    val after = System.currentTimeMillis()
    // jackson で Response に parse
    val booksResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
    logger.info("TEST-TEST:${test5}: response-time=${after - before} ms, response-size=${booksResponse.books.size} , db-target-count= may be 1000")
    booksResponse.books.size shouldBe 1000
  }

}) {
  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthorBooksControllerTest::class.java)!!

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