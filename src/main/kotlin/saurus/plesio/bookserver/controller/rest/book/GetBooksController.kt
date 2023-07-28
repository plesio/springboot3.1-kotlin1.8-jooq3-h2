package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.openapi.generated.controller.GetBooksApi
import saurus.plesio.bookserver.openapi.generated.model.BooksResponse
import saurus.plesio.bookserver.service.BookService

@RestController
class GetBooksController(
  @Autowired val bookService: BookService
) : GetBooksApi {
  override fun getBooks(bookTitle: String?, isbnCode: String?): ResponseEntity<BooksResponse> {
    logger.info("getBooks: bookTitle: $bookTitle, isbnCode: $isbnCode")
    val books = bookService.getBooks(bookTitle, isbnCode)
    return ResponseEntity(BooksResponse(books), HttpStatus.OK)
  }

  companion object {
    val logger = LoggerFactory.getLogger(GetBooksController::class.java)!!
  }
}
