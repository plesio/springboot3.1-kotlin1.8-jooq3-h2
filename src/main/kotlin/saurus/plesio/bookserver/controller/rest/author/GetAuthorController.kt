package saurus.plesio.bookserver.controller.rest.author

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.service.AuthorService

@RestController
class GetAuthorController(
  @Autowired val authorService: AuthorService
) : GetAuthorApi {
    override fun getAuthor(authorId: String): ResponseEntity<Author> {
    logger.info("getAuthor: authorId: $authorId")
    val author = authorService.getAuthor(authorId)
    return if (author != null) ResponseEntity(author, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(GetAuthorController::class.java)!!
  }
}

