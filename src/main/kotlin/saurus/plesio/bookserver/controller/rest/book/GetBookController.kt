package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book

@RestController
class GetBookController : GetBookApi {
  companion object {
    val logger = LoggerFactory.getLogger(GetBookController::class.java)!!
  }

  @Autowired
  lateinit var bookRepository: BookRepository

  override fun getBook(bookId: String): ResponseEntity<Book> {
    logger.info("getBook: bookId: $bookId")
    val book = bookRepository.findById(bookId)?.let {
      when {
        it.bookId.isNullOrBlank() -> null
        else -> Book(
          bookId = it.bookId!!,
          bookTitle = it.bookTitle!!,
          isbnCode = it.isbnCode ?: "",
          publishedDate = it.publishedDate!!,
          remarks = it.remarks ?: ""
        )
      }
    }
    return book?.let {
      ResponseEntity(it, HttpStatus.OK)
    } ?: ResponseEntity(HttpStatus.NOT_FOUND)
  }

}

