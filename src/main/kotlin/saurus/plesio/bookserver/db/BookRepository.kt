package saurus.plesio.bookserver.db

import com.github.guepardoapps.kulid.ULID
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.pojos.Book
import saurus.plesio.bookserver.jooq.tables.references.AUTHOR_BOOK
import saurus.plesio.bookserver.jooq.tables.references.BOOK
import java.time.LocalDate

@Repository
class BookRepository(
  private val dslContext: DSLContext
) {

  fun findById(bookId: String): Book? {
    return this.dslContext.select().from(BOOK).where(BOOK.BOOK_ID.eq(bookId)).fetchOne()?.let { toModel(it) }
  }

  fun list(bookTitle: String?, isbnCode: String?): List<Book> {
    val isTitleBlank = bookTitle.isNullOrBlank()
    val isIsbnCodeBlank = isbnCode.isNullOrBlank()

    return if (isTitleBlank && isIsbnCodeBlank) {
      listAll()
    } else if (isTitleBlank) {
      listByIsbnCode(isbnCode)
    } else if (isIsbnCodeBlank) {
      listByLikeTitle(bookTitle)
    } else {
      this.dslContext.select().from(BOOK).where(
        BOOK.BOOK_TITLE.like("%$bookTitle%").and(BOOK.ISBN_CODE.eq(isbnCode))).fetch()
        .map { toModel(it) }
    }
  }

  fun listByLikeTitle(bookTitle: String? = null): List<Book> {
    return this.dslContext.select().from(BOOK).where(
      (if (bookTitle.isNullOrBlank()) DSL.trueCondition() else BOOK.BOOK_TITLE.like("%$bookTitle%"))
    ).fetch().map { toModel(it) }
  }

  fun listByIsbnCode(isbnCode: String? = null): List<Book> {
    return this.dslContext.select().from(BOOK).where(
      (if (isbnCode.isNullOrBlank()) DSL.trueCondition() else BOOK.ISBN_CODE.eq(isbnCode))
    ).fetch().map { toModel(it) }
  }

  fun listByBooksIds(bookIds: List<String>): List<Book> {
    return this.dslContext.select().from(BOOK).where(BOOK.BOOK_ID.`in`(bookIds)).fetch().map { toModel(it) }
  }

  fun listAll(): List<Book> {
    return this.dslContext.select().from(BOOK).fetch().map { toModel(it) }
  }

  /**
   * Book の insert 時には AuthorBook の insert 処理も行う.
   */
  fun insert(
    authorId: String,
    bookTitle: String,
    isbnCode: String?,
    publishedDate: LocalDate?,
    remarks: String?
  ): String {
    val newBookId = ULID.random()
    dslContext.transaction { c ->
      DSL.using(c)
        .newRecord(BOOK).also {
          it.bookId = newBookId
          it.bookTitle = bookTitle
          it.isbnCode = isbnCode ?: ""
          it.publishedDate = publishedDate
          it.remarks = remarks ?: ""
          it.store()
        }

      DSL.using(c)
        .newRecord(AUTHOR_BOOK).also {
          it.authorId = authorId
          it.bookId = newBookId
          it.store()
        }
    }
    return newBookId
  }

  fun update(book: Book): Int {
    return this.dslContext.update(BOOK).set(BOOK.BOOK_TITLE, book.bookTitle).set(BOOK.ISBN_CODE, book.isbnCode)
      .set(BOOK.PUBLISHED_DATE, book.publishedDate).set(BOOK.REMARKS, book.remarks).where(BOOK.BOOK_ID.eq(book.bookId))
      .execute()
  }

  fun delete(bookId: String): Int {
    this.dslContext.deleteFrom(AUTHOR_BOOK).where(AUTHOR_BOOK.BOOK_ID.eq(bookId)).execute()
    return dslContext.deleteFrom(BOOK).where(BOOK.BOOK_ID.eq(bookId)).execute();
  }

  fun deleteAll(): Int {
    this.dslContext.deleteFrom(AUTHOR_BOOK).execute() // こっちも削除することになるけど、こっちは返さない。
    return dslContext.deleteFrom(BOOK).execute()
  }


  private fun toModel(record: Record) = Book(
    record.getValue(BOOK.BOOK_ID)!!,
    record.getValue(BOOK.BOOK_TITLE)!!,
    record.getValue(BOOK.ISBN_CODE),
    record.getValue(BOOK.PUBLISHED_DATE),
    record.getValue(BOOK.REMARKS),
  )

}
