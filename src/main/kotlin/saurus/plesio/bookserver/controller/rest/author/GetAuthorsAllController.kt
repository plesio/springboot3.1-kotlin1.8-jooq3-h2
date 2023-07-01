package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorsAllApi
import saurus.plesio.bookserver.openapi.generated.model.AuthorsResponse


@RestController
class GetAuthorsAllController : GetAuthorsAllApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun getAuthorsAll(): ResponseEntity<AuthorsResponse> {
    return ResponseEntity(AuthorsResponse(authors = authorRepository.findAll().map {
      saurus.plesio.bookserver.openapi.generated.model.Author(
        authorId = it.authorId,
        firstName = it.firstName,
        lastName = it.lastName,
        birthYear = it.birthYear,
        remarks = it.remarks
      )
    }), HttpStatus.OK)
  }

}

