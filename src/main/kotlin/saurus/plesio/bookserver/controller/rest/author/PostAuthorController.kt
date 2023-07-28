package saurus.plesio.bookserver.controller.rest.author

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.openapi.generated.controller.PostInsertAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse
import saurus.plesio.bookserver.service.AuthorService

@RestController
class PostAuthorController(
  @Autowired val authorService: AuthorService
) : PostInsertAuthorApi {

  override fun postInsertAuthor(author: Author): ResponseEntity<AuthorIdResponse> {
    logger.info("postInsertAuthor: author: $author")
    authorService.validatePostInsertAuthor(author)
    //
    return try {
      val newAuthorId = authorService.insertAuthor(author)
      ResponseEntity(AuthorIdResponse(newAuthorId), HttpStatus.OK)
    } catch (e: Exception) {
      logger.error("postInsertAuthor: author: $author", e)
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }

  companion object {
    val logger = LoggerFactory.getLogger(PostAuthorController::class.java)!!
  }

}

