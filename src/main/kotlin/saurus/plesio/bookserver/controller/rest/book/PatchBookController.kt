package saurus.plesio.bookserver.controller.rest.book

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse


@RestController
class PatchBookController : PatchUpdateBookApi {
  @Autowired
  lateinit var bookRepository: BookRepository

  override fun patchUpdateBook(bookId: String, book: Book): ResponseEntity<BookIdResponse> {
    if (bookId.isBlank() || bookId != book.bookId) {
      return ResponseEntity(HttpStatus.NOT_FOUND)
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
      ResponseEntity(HttpStatus.BAD_REQUEST)
    }
  }

}