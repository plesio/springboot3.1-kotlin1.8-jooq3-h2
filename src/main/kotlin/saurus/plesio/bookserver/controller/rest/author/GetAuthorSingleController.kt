package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorSingleApi
import saurus.plesio.bookserver.openapi.generated.model.Author


@RestController
class GetAuthorSingleController : GetAuthorSingleApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun getAuthorSingle(authorId: String): ResponseEntity<Author> {
    val author = authorRepository.findById(authorId)?.let {
      when {
        it.authorId.isNullOrBlank() -> null
        else -> Author(
          authorId = it.authorId!!,
          firstName = it.firstName ?: "",
          lastName = it.lastName ?: "",
          birthYear = it.birthYear,
          remarks = it.remarks ?: ""
        )
      }
    }

    return author?.let {
      ResponseEntity(it, HttpStatus.OK)
    } ?: ResponseEntity(HttpStatus.NOT_FOUND)
  }
}

