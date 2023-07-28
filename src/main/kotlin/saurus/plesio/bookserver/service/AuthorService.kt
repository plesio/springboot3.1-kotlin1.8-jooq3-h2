package saurus.plesio.bookserver.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import saurus.plesio.bookserver.db.AuthorRepository
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR
import saurus.plesio.bookserver.openapi.generated.model.Author

@Service
class AuthorService(
  @Autowired val authorRepository: AuthorRepository,
) {

  fun getAuthor(authorId: String): Author? {
    return authorRepository.findById(authorId)?.let(::toModel)
  }

  fun getAuthors(authorName: String?): List<Author> {
    return authorRepository.listByLikeName(authorName).map(::toModel)
  }

  @Transactional
  fun updateAuthor(openapiAuthor: Author): String {
    val (id, name, birthYear, remarks) = openapiAuthor
    val author = saurus.plesio.bookserver.model.Author(id, name ?: "", birthYear, remarks ?: "")
    authorRepository.update(author)
    return id
  }

  @Transactional
  fun insertAuthor(author: Author): String {
    val id = authorRepository.insert(author.authorName, author.birthYear, author.remarks ?: "")
    return id
  }

  @Throws(ResponseStatusException::class)
  fun validatePatchUpdateAuthor(authorId: String, author: Author) {
    if (authorId.isBlank() || authorId != author.authorId) {
      throw ResponseStatusException(HttpStatus.NOT_FOUND, "authorId is not match.")
    }
    // validation - varchar max length
    val authorNameMaxLength = AUTHOR.field(AUTHOR.AUTHOR_NAME)?.dataType?.length() ?: -1
    if (authorNameMaxLength < author.authorName.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is too long. (MAX: ${authorNameMaxLength})")
    }
  }

  @Throws(ResponseStatusException::class)
  fun validatePostInsertAuthor(author: Author) {
    if (author.authorName.isBlank()) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is blank")
    }
    // validation - varchar max length
    val authorNameMaxLength = AUTHOR.field(AUTHOR.AUTHOR_NAME)?.dataType?.length() ?: -1
    if (authorNameMaxLength < author.authorName.length) {
      throw ResponseStatusException(HttpStatus.BAD_REQUEST, "authorName is too long. (MAX: ${authorNameMaxLength})")
    }
  }

  // ---------------------------------------------------------
  private fun toModel(model: saurus.plesio.bookserver.model.Author): Author {
    val (authorId, authorName, birthYear, remarks) = model
    return Author(authorId = authorId, authorName = authorName, birthYear = birthYear, remarks = remarks)
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthorService::class.java)!!
  }
}