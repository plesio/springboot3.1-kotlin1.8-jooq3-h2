package saurus.plesio.bookserver.controller.rest.book

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetBooksApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BooksResponse

@RestController
class GetBooksController : GetBooksApi {
  companion object {
    val logger = LoggerFactory.getLogger(GetBooksController::class.java)!!
  }

  @Autowired
  lateinit var bookRepository: BookRepository

  override fun getBooks(bookTitle: String?, isbnCode: String?): ResponseEntity<BooksResponse> {
    logger.info("getBooks: bookTitle: $bookTitle, isbnCode: $isbnCode")
    return ResponseEntity(BooksResponse(books = bookRepository.list(bookTitle, isbnCode).map {
      Book(
        bookId = it.bookId!!,
        bookTitle = it.bookTitle!!,
        isbnCode = it.isbnCode ?: "",
        publishedDate = it.publishedDate!!,
        remarks = it.remarks ?: ""
      )
    }), HttpStatus.OK)
  }
}
