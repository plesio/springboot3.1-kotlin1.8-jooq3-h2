package saurus.plesio.bookserver.controller.rest.author

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorsApi
import saurus.plesio.bookserver.openapi.generated.model.AuthorsResponse
import saurus.plesio.bookserver.service.AuthorService

@RestController
class GetAuthorsController(
  @Autowired val authorService: AuthorService,
) : GetAuthorsApi {
  override fun getAuthors(authorName: String?): ResponseEntity<AuthorsResponse> {
    logger.info("getAuthors: authorName: $authorName")
    val authors = authorService.getAuthors(authorName)
    return ResponseEntity(AuthorsResponse(authors), HttpStatus.OK)
  }

  companion object {
    val logger = LoggerFactory.getLogger(GetAuthorsController::class.java)!!
  }
}

