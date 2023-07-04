package saurus.plesio.bookserver.controller.rest.author

import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.jooq.tables.Book
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import saurus.plesio.bookserver.openapi.generated.controller.PostInsertAuthorApi
import saurus.plesio.bookserver.openapi.generated.model.Author
import saurus.plesio.bookserver.openapi.generated.model.AuthorIdResponse


@RestController
class PostAuthorController : PostInsertAuthorApi {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  override fun postInsertAuthor(author: Author): ResponseEntity<AuthorIdResponse> {
    if (author.authorName.isBlank()) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is blank")
    }
    // validation - varchar max length
    val authorNameMaxLength = DSL.field(AUTHOR.AUTHOR_NAME.name).dataType.length()
    if (authorNameMaxLength < author.authorName.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is too long. (MAX: ${authorNameMaxLength})")
    }

    return try {
      val newAuthorId = authorRepository.insert(author.authorName, author.birthYear, author.remarks ?: "")
      ResponseEntity(AuthorIdResponse(authorId = newAuthorId), HttpStatus.OK)
    } catch (e: Exception) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
    }
  }

}

