package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.openapi.generated.controller.GetBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.service.BookService

@RestController
class GetBookController(
  @Autowired val bookService: BookService
) : GetBookApi {

  override fun getBook(bookId: String): ResponseEntity<Book> {
    logger.info("getBook: bookId: $bookId")
    val book = bookService.getBook(bookId)
    return if (book != null) ResponseEntity(book, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
  }

  companion object {
    val logger = LoggerFactory.getLogger(GetBookController::class.java)!!
  }
}

