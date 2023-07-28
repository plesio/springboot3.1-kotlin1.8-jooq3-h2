package saurus.plesio.bookserver.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import saurus.plesio.bookserver.openapi.generated.model.Book

@Service
class BookService(
  @Autowired val bookRepository: BookRepository,
) {

  fun getBook(bookId: String): Book? {
    return bookRepository.findById(bookId)?.let(::toModel)
  }

  fun getBooks(bookTitle: String?, isbnCode: String?): List<Book> {
    return bookRepository.list(bookTitle, isbnCode).map(::toModel)
  }

  @Transactional
  fun updateBook(openapiBook: Book): String {
    val (id, title, isbn, date, remarks) = openapiBook
    val book = saurus.plesio.bookserver.model.Book(id ?: "", title ?: "", isbn, date, remarks)
    bookRepository.update(book)
    return id ?: ""
  }

  @Throws(ResponseStatusException::class)
  fun validatePatchUpdateBook(bookId: String, book: Book) {
    if (bookId.isBlank() || bookId != book.bookId) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "bookId is not match.")
    }
    // validation - varchar max length
    val isbnCodeMaxLength = BOOK.field(BOOK.ISBN_CODE)?.dataType?.length() ?: -1
    if (book.isbnCode != null && isbnCodeMaxLength < book.isbnCode.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "isbnCode is too long. (MAX: ${isbnCodeMaxLength})")
    }
  }

  // ---------------------------------------------------------
  private fun toModel(model: saurus.plesio.bookserver.model.Book): Book {
    val (id, title, isbn, publishedDate, remarks) = model
    return Book(bookId = id, bookTitle = title, isbnCode = isbn, publishedDate = publishedDate, remarks = remarks)
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(BookService::class.java)!!
  }
}