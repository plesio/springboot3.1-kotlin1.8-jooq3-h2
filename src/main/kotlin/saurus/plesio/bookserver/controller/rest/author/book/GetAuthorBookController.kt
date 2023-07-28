package saurus.plesio.bookserver.controller.rest.author.book


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorBooksApi
import saurus.plesio.bookserver.openapi.generated.model.BooksResponse
import saurus.plesio.bookserver.service.AuthorBookService

@RestController
class GetAuthorBookController(
  @Autowired val authorBookService: AuthorBookService,
) : GetAuthorBooksApi {
  companion object {
    val logger: Logger = LoggerFactory.getLogger(GetAuthorBookController::class.java)!!
  }

  @Throws(ResponseStatusException::class)
  override fun getAuthorBooks(authorId: String, bookTitle: String?, isbnCode: String?): ResponseEntity<BooksResponse> {
    logger.info("getAuthorBooks: authorId: $authorId, bookTitle: $bookTitle, isbnCode: $isbnCode")
    authorBookService.validateGetAuthorBooks(authorId)
    //
    val books = authorBookService.getBooks(authorId, bookTitle, isbnCode)
    return ResponseEntity(BooksResponse(books = books), HttpStatus.OK)
  }


}

