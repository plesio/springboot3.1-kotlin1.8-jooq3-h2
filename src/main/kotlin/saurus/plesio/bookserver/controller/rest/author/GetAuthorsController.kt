package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorsApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorsResponse


@RestController
class GetAuthorsController : GetAuthorsApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun getAuthors(authorName: String?): ResponseEntity<AuthorsResponse> {
    return ResponseEntity(AuthorsResponse(authors = authorRepository.listByLikeName(authorName).map {
      Author(
        authorId = it.authorId,
        authorName = it.authorName!!,
        birthYear = it.birthYear,
        remarks = it.remarks
      )
    }), HttpStatus.OK)
  }

}

