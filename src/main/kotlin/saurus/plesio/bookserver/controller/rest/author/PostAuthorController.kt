package saurus.plesio.bookserver.controller.rest.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.openapi.generated.controller.GetAuthorApi
import saurus.plesio.bookserver.openapi.generated.controller.PatchUpdateAuthorApi
import saurus.plesio.bookserver.openapi.generated.controller.PostInsertAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse
import saurus.plesio.bookserver.openapi.generated.model.BookIdResponse


@RestController
class PostAuthorController : PostInsertAuthorApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun postInsertAuthor(author: Author): ResponseEntity<AuthorIdResponse> {
    return try {
      val newAuthorId = authorRepository.insert(author.authorName, author.birthYear, author.remarks ?: "")
      ResponseEntity(AuthorIdResponse(authorId = newAuthorId), HttpStatus.OK)
    } catch (e: Exception) {
      ResponseEntity(HttpStatus.BAD_REQUEST)
    }
  }

}

