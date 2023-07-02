package saurus.plesio.bookserver.db

import com.github.guepardoapps.kulid.ULID
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.Author.Companion.AUTHOR
import saurus.plesio.bookserver.jooq.tables.pojos.Author

@Repository
class AuthorRepository(
  private val dslContext: DSLContext
) {

  fun findById(authorId: String): Author? {
    return this.dslContext.select()
      .from(AUTHOR)
      .where(AUTHOR.AUTHOR_ID.eq(authorId))
      .fetchOne()?.let { toModel(it) }
  }

  fun findBytName(authorName: String? = null): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .where(
        if (authorName != null) {
          AUTHOR.AUTHOR_NAME.eq(authorName)
        } else {
          DSL.trueCondition()
        }
      )
      .fetch().map { toModel(it) }
  }

  fun findAll(): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .fetch().map { toModel(it) }
  }

  fun insert(authorName: String, birthYear: Int?, remarks: String): Author {
    val record = this.dslContext.newRecord(AUTHOR).also {
      it.authorId = ULID.random()
      it.authorName = authorName
      it.birthYear = birthYear
      it.remarks = remarks
      it.store()
    }
    return Author(record.authorId!!, record.authorName!!, record.birthYear, record.remarks)
  }

  fun update(author: Author) {
    this.dslContext.update(AUTHOR)
      .set(AUTHOR.AUTHOR_NAME, author.authorName)
      .set(AUTHOR.BIRTH_YEAR, author.birthYear)
      .set(AUTHOR.REMARKS, author.remarks)
      .where(AUTHOR.AUTHOR_ID.eq(author.authorId))
      .execute()
  }


  fun deleteAll() {
    this.dslContext.deleteFrom(AUTHOR).execute()
  }


  private fun toModel(record: Record) = Author(
    record.getValue(AUTHOR.AUTHOR_ID),
    record.getValue(AUTHOR.AUTHOR_NAME),
    record.getValue(AUTHOR.BIRTH_YEAR),
    record.getValue(AUTHOR.REMARKS),
  )
}
