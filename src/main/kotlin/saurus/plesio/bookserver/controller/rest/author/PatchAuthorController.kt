package saurus.plesio.bookserver.controller.rest.author

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse
import saurus.plesio.bookserver.service.AuthorService

@RestController
class PatchAuthorController(
  @Autowired val authorService: AuthorService
) : PatchUpdateAuthorApi {

  override fun patchUpdateAuthor(authorId: String, author: Author): ResponseEntity<AuthorIdResponse> {
    logger.info("patchUpdateAuthor: authorId: $authorId, author: $author")
    authorService.validatePatchUpdateAuthor(authorId, author)
    //
    return try {
      val res = AuthorIdResponse(authorService.updateAuthor(author))
      ResponseEntity(res, HttpStatus.OK)
    } catch (e: Exception) {
      logger.error("patchUpdateAuthor: authorId: $authorId, author: $author", e)
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }

  companion object {
    val logger = LoggerFactory.getLogger(PatchAuthorController::class.java)!!
  }
}

