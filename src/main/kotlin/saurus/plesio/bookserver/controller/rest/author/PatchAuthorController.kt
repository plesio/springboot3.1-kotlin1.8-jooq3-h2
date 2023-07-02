package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorApi
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse


@RestController
class PatchAuthorController : PatchUpdateAuthorApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun patchUpdateAuthor(authorId: String, author: Author): ResponseEntity<AuthorIdResponse> {
    if (authorId.isBlank() || authorId != author.authorId) {
      return ResponseEntity(HttpStatus.NOT_FOUND)
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
      ResponseEntity(HttpStatus.BAD_REQUEST)
    }
  }



}

