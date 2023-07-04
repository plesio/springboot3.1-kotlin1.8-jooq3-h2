@file:Suppress("unused")
package saurus.plesio.bookserver.db

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.pojos.AuthorBook
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR_BOOK
import saurus.plesio.bookserver.jooq.tables.references.BOOK

@Repository
class AuthorBookRepository(
  private val dslContext: DSLContext
) {

  fun listByAuthorId(authorId: String): List<AuthorBook> {
    return this.dslContext.select()
      .from(AUTHOR_BOOK)
      .where(AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
      .fetch().map { toModel(it) }
  }

  private fun toModel(record: Record) = AuthorBook(
    record.getValue(AUTHOR_BOOK.AUTHOR_ID)!!,
    record.getValue(BOOK.BOOK_ID)!!,
  )
}
