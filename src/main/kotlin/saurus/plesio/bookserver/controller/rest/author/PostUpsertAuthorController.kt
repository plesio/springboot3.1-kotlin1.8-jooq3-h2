package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.PostUpsertAuthorSingleApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorUpsertResponse


@RestController
class PostUpsertAuthorController : PostUpsertAuthorSingleApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun postUpsertAuthorSingle(author: Author): ResponseEntity<AuthorUpsertResponse> {
    try {
      val authorId = if (author.authorId.isNullOrBlank()) {
        // insert
        val res = authorRepository.insert(
          author.firstName ?: "",
          author.lastName ?: "",
          author.birthYear,
          author.remarks ?: "",
        )
        res.authorId!!
      } else {
        // update
        authorRepository.update(
          author.authorId,
          author.firstName ?: "",
          author.lastName ?: "",
          author.birthYear,
          author.remarks ?: "",
        )
        author.authorId
      }
      return ResponseEntity(AuthorUpsertResponse(authorId), HttpStatus.OK)
    } catch (e: Exception) {
      return ResponseEntity(HttpStatus.BAD_REQUEST)
    }
  }
}