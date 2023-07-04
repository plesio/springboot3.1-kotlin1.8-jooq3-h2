package saurus.plesio.bookserver.controller.rest.author

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse

@RestController
class PatchAuthorController : PatchUpdateAuthorApi {
  companion object {
    val logger = LoggerFactory.getLogger(PatchAuthorController::class.java)!!
  }

  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun patchUpdateAuthor(authorId: String, author: Author): ResponseEntity<AuthorIdResponse> {
    logger.info("patchUpdateAuthor: authorId: $authorId, author: $author")
    if (authorId.isBlank() || authorId != author.authorId) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
    }
    // validation - varchar max length
    val authorNameMaxLength = AUTHOR.field(AUTHOR.AUTHOR_NAME)?.dataType?.length() ?: -1
    if (authorNameMaxLength < author.authorName.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is too long. (MAX: ${authorNameMaxLength})")
    }

    return try {
      author.let {
        saurus.plesio.bookserver.jooq.tables.pojos.Author(
          authorId = it.authorId!!,
          authorName = it.authorName,
          birthYear = it.birthYear,
          remarks = it.remarks ?: ""
        )
      }.let(authorRepository::update)
      ResponseEntity(AuthorIdResponse(authorId = authorId), HttpStatus.OK)
    } catch (e: Exception) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }


}

