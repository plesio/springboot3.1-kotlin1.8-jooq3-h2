package saurus.plesio.bookserver.rest.book

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
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec


@Suppress("unused")
@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Testcontainers
internal class BooksControllerTest(
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

  test("simple get and check empty.") {
    mockMvc.get("/api/v1/books")
      .andExpect {
        status { isOk() }
      }
      .andReturn()
      .response
      .contentAsString shouldBe """{"books":[]}"""
  }
//
//  val test2 = "find from 1000 records and check response."
//  test(test2) {
//    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
//
//    val bookTitles1000 = (1..999).map { generateBookTitle() }.plus("世界の果てまでいけない8")
//    bookTitles1000.map { bookTitle ->
//      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
//      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
//    }
//    val targetFirstNameSize = bookTitles1000.filter { it.contains("世界") }.size
//
//    val before = System.currentTimeMillis();
//    val resultStr = mockMvc.get("/api/v1/authors?bookTitle=世界")
//      .andExpect {
//        status { isOk() }
//      }
//      .andReturn()
//      .response
//      .contentAsString
//    val after = System.currentTimeMillis();
//    // jackson で List<Author> に parse
//    val authorsResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
//    logger.info("${test2}: response-time=${after - before} ms, target=山田, response-size=${authorsResponse.books.size} , db-target-count=$targetFirstNameSize")
//    authorsResponse.books.size shouldBe targetFirstNameSize
//  }
//
//  val test3 = "find from 10000 records and check response."
//  test(test3) {
//    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
//
//    val bookTitles10000 = (1..9999).map { generateBookTitle() }.plus("世界の果てまでいけない8")
//    bookTitles10000.map { bookTitle ->
//      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
//      bookRepository.insert(dummyAuthorId, bookTitle, null, publishDate, "")
//    }
//    val targetFirstNameSize = bookTitles10000.filter { it.contains("世界") }.size
//
//    val before = System.currentTimeMillis();
//    val resultStr = mockMvc.get("/api/v1/authors?bookTitle=世界")
//      .andExpect {
//        status { isOk() }
//      }
//      .andReturn()
//      .response
//      .contentAsString
//    val after = System.currentTimeMillis();
//    // jackson で List<Author> に parse
//    val authorsResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
//    logger.info("${test3}: response-time=${after - before} ms, target=山田, response-size=${authorsResponse.books.size} , db-target-count=$targetFirstNameSize")
//    authorsResponse.books.size shouldBe targetFirstNameSize
//  }
//
//  val test4 = "find by ISBN from 10000 records and check response."
//  test(test4) {
//    val dummyAuthorId = authorRepository.insert("山田 光三郎", 1960, "")
//    val bookTitles9999 = (1..9999).map { generateBookTitle() }
//    // 重複しないように 13桁の ISBN を生成
//    val isbnCodes9999 = (1..9999).map { (1..9).map { (0..8).random() }.joinToString("") + String.format("%05d", it) }
//    bookTitles9999.mapIndexed() { idx, bookTitle ->
//      val publishDate = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
//      bookRepository.insert(dummyAuthorId, bookTitle, isbnCodes9999[idx], publishDate, "")
//    }
//    // 手動で1個追加
//    val targetIsbn = "978" + (1..9).map { (0..8).random() }.joinToString("") + String.format("%05d", 10000).apply {
//      val d = LocalDate.of((1960..2011).random(), (1..12).random(), (1..28).random())
//      bookRepository.insert(dummyAuthorId, "世界の果てまでいけない8", this, d, "")
//    }
//
//    val before = System.currentTimeMillis();
//    val resultStr = mockMvc.get("/api/v1/authors?isbnCode=${targetIsbn}")
//      .andExpect {
//        status { isOk() }
//      }
//      .andReturn()
//      .response
//      .contentAsString
//    val after = System.currentTimeMillis();
//    // jackson で List<Author> に parse
//    val authorsResponse = customObjectMapper.readValue(resultStr, BooksResponse::class.java)
//    logger.info("${test3}: response-time=${after - before} ms, target=${targetIsbn}, response-size=${authorsResponse.books.size}")
//    authorsResponse.books.size shouldBe 1
//  }
//

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