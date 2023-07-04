package saurus.plesio.bookserver.controller.rest.author.book


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorBookRepository
import saurus.plesio.bookserver.db.BookRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorBooksApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BooksResponse


@RestController
class GetAuthorBookController : GetAuthorBooksApi {
  @Autowired
  lateinit var authorBookRepository: AuthorBookRepository

  @Autowired
  lateinit var bookRepository: BookRepository

  override fun getAuthorBooks(authorId: String, bookTitle: String?, isbnCode: String?): ResponseEntity<BooksResponse> {
    if (authorId.isBlank()) {
      return ResponseEntity(HttpStatus.NOT_FOUND)
    }
    // MEMO: その Author が存在するかの判定はしない.
    val bookIds = authorBookRepository.listByAuthorId(authorId).mapNotNull { it.bookId }
    val books = bookRepository.listByBooksIds(bookIds).asSequence() //
      .filter { book ->
        when {
          bookTitle.isNullOrBlank() -> true
          else -> (book.bookTitle ?: "").contains(bookTitle)
        }
      }.filter { book ->
        when {
          isbnCode.isNullOrBlank() -> true
          else -> book.isbnCode == isbnCode
        }
      }.map {
        Book(
          bookId = it.bookId!!,
          bookTitle = it.bookTitle!!,
          isbnCode = it.isbnCode ?: "",
          publishedDate = it.publishedDate!!,
          remarks = it.remarks ?: ""
        )
      }.toList()

    return ResponseEntity(BooksResponse(books = books), HttpStatus.OK)
  }


}

