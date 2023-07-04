package saurus.plesio.bookserver.controller.rest.author.book


import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.AuthorBookRepository
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import saurus.plesio.bookserver.openapi.generated.controller.PostInsertAuthorBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse


@RestController
class PostAuthorBookController : PostInsertAuthorBookApi {
  @Autowired
  lateinit var authorBookRepository: AuthorBookRepository

  @Autowired
  lateinit var bookRepository: BookRepository

  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun postInsertAuthorBook(authorId: String, book: Book): ResponseEntity<BookIdResponse> {
    // validation - exists params
    if (authorId.isBlank()) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
    } else if (book.bookTitle.isNullOrBlank()) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "bookTitle is blank..")
    }
    // validation - varchar max length
    val isbnCodeMaxLength = DSL.field(BOOK.ISBN_CODE.name).dataType.length()
    if (book.isbnCode != null && isbnCodeMaxLength < book.isbnCode.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "isbnCode is too long. (MAX: ${isbnCodeMaxLength})")
    }

    // Author の存在確認をする.
    authorRepository.findById(authorId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    return try {
      val newBookId = bookRepository.insert(authorId, book.bookTitle, book.isbnCode, book.publishedDate, book.remarks)
      ResponseEntity(BookIdResponse(bookId = newBookId), HttpStatus.OK)
    } catch (e: Exception) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.localizedMessage)
    }
  }


}

