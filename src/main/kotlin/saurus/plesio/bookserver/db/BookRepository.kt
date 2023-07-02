package saurus.plesio.bookserver.db

import com.github.guepardoapps.kulid.ULID
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.pojos.Book
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import java.time.LocalDate

@Repository
class BookRepository(
  private val dslContext: DSLContext
) {

  fun findById(bookId: String): Book? {
    return this.dslContext.select()
      .from(BOOK)
      .where(BOOK.BOOK_ID.eq(bookId))
      .fetchOne()?.let { toModel(it) }
  }

  fun findBytName(bookTitle: String? = null): List<Book> {
    return this.dslContext.select()
      .from(BOOK)
      .where(
        if (bookTitle != null) {
          BOOK.BOOK_TITLE.eq(bookTitle)
        } else {
          DSL.trueCondition()
        }
      )
      .fetch().map { toModel(it) }
  }

  fun findAll(): List<Book> {
    return this.dslContext.select()
      .from(BOOK)
      .fetch().map { toModel(it) }
  }

  fun insert(bookTitle: String, isbnCode: String?, publishedDate: LocalDate?, remarks: String?): Book {
    val record = this.dslContext.newRecord(BOOK).also {
      it.bookId = ULID.random()
      it.bookTitle = bookTitle
      it.isbnCode = isbnCode ?: ""
      it.publishedDate = publishedDate
      it.remarks = remarks ?: ""
      it.store()
    }
    return Book(record.bookId!!, record.bookTitle!!, record.isbnCode, record.publishedDate, record.remarks)
  }

  fun update(book: Book) {
    this.dslContext.update(BOOK)
      .set(BOOK.BOOK_TITLE, book.bookTitle)
      .set(BOOK.ISBN_CODE, book.isbnCode)
      .set(BOOK.PUBLISHED_DATE, book.publishedDate)
      .set(BOOK.REMARKS, book.remarks)
      .where(BOOK.BOOK_ID.eq(book.bookId))
      .execute()
  }


  fun deleteAll() {
    this.dslContext.deleteFrom(BOOK).execute()
  }


  private fun toModel(record: Record) = Book(
    record.getValue(BOOK.BOOK_ID)!!,
    record.getValue(BOOK.BOOK_TITLE)!!,
    record.getValue(BOOK.ISBN_CODE),
    record.getValue(BOOK.PUBLISHED_DATE),
    record.getValue(BOOK.REMARKS),
  )
}
