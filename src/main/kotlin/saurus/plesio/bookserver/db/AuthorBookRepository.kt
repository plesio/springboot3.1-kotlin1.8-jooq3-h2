package saurus.plesio.bookserver.db

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.pojos.Book
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR_BOOK
import saurus.plesio.bookserver.jooq.tables.references.BOOK

@Repository
class AuthorBookRepository(
  private val dslContext: DSLContext
) {

  fun listByAuthorId(authorId: String): List<Book> {
    return this.dslContext.select()
      .from(AUTHOR_BOOK)
      .where(AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
      .fetch().map { toModel(it) }
  }

  private fun toModel(record: Record) = Book(
    record.getValue(BOOK.BOOK_ID)!!,
    record.getValue(BOOK.BOOK_TITLE)!!,
    record.getValue(BOOK.ISBN_CODE),
    record.getValue(BOOK.PUBLISHED_DATE),
    record.getValue(BOOK.REMARKS),
  )
}
