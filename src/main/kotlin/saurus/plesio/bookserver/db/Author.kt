package saurus.plesio.bookserver.db

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
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

  fun findBytName(firstName: String? = null, lastName: String? = null): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .where(
        if (firstName != null) {
          AUTHOR.FIRST_NAME.eq(firstName)
        } else {
          DSL.trueCondition()
        }.and(
          if (lastName != null) {
            AUTHOR.LAST_NAME.eq(lastName)
          } else {
            DSL.trueCondition()
          }
        )
      )
      .fetch().map { toModel(it) }
  }

  fun findAll(): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .fetch().map { toModel(it) }
  }

  fun insert(firstName: String, lastName: String, birthYear: Int?, remarks: String): Author {
    val record = this.dslContext.newRecord(AUTHOR).also {
      it.authorId = ULID.random()
      it.firstName = firstName
      it.lastName = lastName
      it.birthYear = birthYear
      it.remarks = remarks
      it.store()
    }
    return Author(record.authorId!!, record.firstName!!, record.lastName!!, record.birthYear!!, record.remarks!!)
  }

  fun update(authorId:String,firstName: String, lastName: String, birthYear: Int?, remarks: String) {
    this.dslContext.update(AUTHOR)
      .set(AUTHOR.FIRST_NAME, firstName)
      .set(AUTHOR.LAST_NAME, lastName)
      .set(AUTHOR.BIRTH_YEAR, birthYear)
      .set(AUTHOR.REMARKS, remarks)
      .where(AUTHOR.AUTHOR_ID.eq(authorId))
      .execute()
    return
  }



  fun deleteAll() {
    this.dslContext.deleteFrom(AUTHOR).execute()
  }


  private fun toModel(record: Record) = Author(
    record.getValue(AUTHOR.AUTHOR_ID)!!,
    record.getValue(AUTHOR.FIRST_NAME)!!,
    record.getValue(AUTHOR.LAST_NAME)!!,
    record.getValue(AUTHOR.BIRTH_YEAR)!!,
    record.getValue(AUTHOR.REMARKS)!!,
  )
}
