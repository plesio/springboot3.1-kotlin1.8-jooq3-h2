package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse
import saurus.plesio.bookserver.service.BookService

@RestController
class PatchBookController(
  @Autowired val bookService: BookService
) : PatchUpdateBookApi {
  override fun patchUpdateBook(bookId: String, book: Book): ResponseEntity<BookIdResponse> {
    logger.info("patchUpdateBook: bookId: $bookId, book: $book")
    bookService.validatePatchUpdateBook(bookId, book)

    return try {
      val res = BookIdResponse(bookService.updateBook(book))
      ResponseEntity(res, HttpStatus.OK)
    } catch (e: Exception) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }

  companion object {
    val logger = LoggerFactory.getLogger(PatchBookController::class.java)!!
  }
}