package saurus.plesio.bookserver.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.AuthorBookRepository
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import saurus.plesio.bookserver.openapi.generated.model.Book

@Service
class AuthorBookService(
  @Autowired val authorRepository: AuthorRepository,
  @Autowired val bookRepository: BookRepository,
  @Autowired val authorBookRepository: AuthorBookRepository,
) {

  fun getBooks(authorId: String, bookTitle: String?, isbnCode: String?): List<Book> {
    val bookIds = authorBookRepository.listByAuthorId(authorId).map { it.bookId }
    return bookRepository.listByBooksIds(bookIds).asSequence()
      .filter {
        if (bookTitle.isNullOrBlank()) true else it.bookTitle.contains(bookTitle)
      }.filter {
        if (isbnCode.isNullOrBlank()) true else it.isbnCode == isbnCode
      }.map(::toBookModel).toList()
  }

  @Transactional
  fun insertAuthorBook(authorId: String, book: Book): String {
    val (_, title, isbn, date, remarks) = book
    val bookId = bookRepository.insert(authorId, title ?: "", isbn, date, remarks)
    return bookId
  }

  @Throws(ResponseStatusException::class)
  fun validateGetAuthorBooks(authorId: String) {
    if (authorId.isBlank()) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
    }
    // author exists?
    authorRepository.findById(authorId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
  }

  @Throws(ResponseStatusException::class)
  fun validatePostInsertAuthorBook(authorId: String, book: Book) {
    // validation - exists params
    if (authorId.isBlank()) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
    } else if (book.bookTitle.isNullOrBlank()) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "bookTitle is blank..")
    }
    // validation - varchar max length
    val isbnCodeMaxLength = BOOK.field(BOOK.ISBN_CODE)?.dataType?.length() ?: -1
    if (book.isbnCode != null && isbnCodeMaxLength < book.isbnCode.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "isbnCode is too long. (MAX: ${isbnCodeMaxLength})")
    }
    // author exists?
    authorRepository.findById(authorId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
  }

  // ---------------------------------------------------------
  private fun toBookModel(model: saurus.plesio.bookserver.model.Book): Book {
    val (id, title, isbn, publishedDate, remarks) = model
    return Book(bookId = id, bookTitle = title, isbnCode = isbn, publishedDate = publishedDate, remarks = remarks)
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthorBookService::class.java)!!
  }
}