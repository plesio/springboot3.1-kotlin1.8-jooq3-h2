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

  /** 著者名をキーワードから includes 判定で探す.  */
  fun listByLikeName(authorName: String? = null): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .where(
        if (authorName.isNullOrBlank()) {
          DSL.trueCondition()
        } else {
          AUTHOR.AUTHOR_NAME.like("%$authorName%")
        }
      )
      .fetch().map { toModel(it) }
  }

  fun listAll(): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .fetch().map { toModel(it) }
  }

  fun insert(authorName: String, birthYear: Int?, remarks: String): String {
    val record = this.dslContext.newRecord(AUTHOR).also {
      it.authorId = ULID.random()
      it.authorName = authorName
      it.birthYear = birthYear
      it.remarks = remarks
      it.store()
    }
    return record.authorId!!
  }

  fun update(author: Author): Int {
    return this.dslContext.update(AUTHOR)
      .set(AUTHOR.AUTHOR_NAME, author.authorName)
      .set(AUTHOR.BIRTH_YEAR, author.birthYear)
      .set(AUTHOR.REMARKS, author.remarks)
      .where(AUTHOR.AUTHOR_ID.eq(author.authorId))
      .execute()
  }

  fun deleteAll(): Int {
    return this.dslContext.deleteFrom(AUTHOR).execute()
  }


  private fun toModel(record: Record) = Author(
    record.getValue(AUTHOR.AUTHOR_ID),
    record.getValue(AUTHOR.AUTHOR_NAME),
    record.getValue(AUTHOR.BIRTH_YEAR),
    record.getValue(AUTHOR.REMARKS),
  )
}
