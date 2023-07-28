package saurus.plesio.bookserver.controller.rest.author.book


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.openapi.generated.controller.PostInsertAuthorBookApi
import saurus.plesio.bookserver.openapi.generated.model.Book
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse
import saurus.plesio.bookserver.service.AuthorBookService

@RestController
class PostAuthorBookController(
  @Autowired val authorBookService: AuthorBookService,
) : PostInsertAuthorBookApi {

  @Throws(ResponseStatusException::class)
  override fun postInsertAuthorBook(authorId: String, book: Book): ResponseEntity<BookIdResponse> {
    logger.info("postInsertAuthorBook: authorId: $authorId, book: $book")
    authorBookService.validatePostInsertAuthorBook(authorId, book)

    return try {
      val newBookId = authorBookService.insertAuthorBook(authorId, book)
      ResponseEntity(BookIdResponse(newBookId), HttpStatus.OK)
    } catch (e: Exception) {
      logger.error("postInsertAuthorBook: authorId: $authorId, book: $book", e)
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.localizedMessage)
    }
  }

  companion object {
    val logger = LoggerFactory.getLogger(PostAuthorBookController::class.java)!!
  }
}

