package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse

@RestController
class PatchBookController : PatchUpdateBookApi {
  companion object {
    val logger = LoggerFactory.getLogger(PatchBookController::class.java)!!
  }

  @Autowired
  lateinit var bookRepository: BookRepository

  override fun patchUpdateBook(bookId: String, book: Book): ResponseEntity<BookIdResponse> {
    logger.info("patchUpdateBook: bookId: $bookId, book: $book")
    if (bookId.isBlank() || bookId != book.bookId) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "bookId is not match.")
    }
    // validation - varchar max length
    val isbnCodeMaxLength = BOOK.field(BOOK.ISBN_CODE)?.dataType?.length() ?: -1
    if (book.isbnCode != null && isbnCodeMaxLength < book.isbnCode.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "isbnCode is too long. (MAX: ${isbnCodeMaxLength})")
    }
    return try {
      book.let {
        saurus.plesio.bookserver.jooq.tables.pojos.Book(
          bookId = it.bookId!!,
          bookTitle = it.bookTitle!!,
          isbnCode = it.isbnCode ?: "",
          publishedDate = it.publishedDate!!,
          remarks = it.remarks ?: ""
        )
      }.let(bookRepository::update)
      ResponseEntity(BookIdResponse(bookId = bookId), HttpStatus.OK)
    } catch (e: Exception) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }

}