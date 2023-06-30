package saurus.plesio.bookserver.db

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import saurus.plesio.bookserver.jooq.tables.Author.Companion.AUTHOR
import saurus.plesio.bookserver.jooq.tables.pojos.Author

@Repository
class AuthorRepository(
  private val dslContext: DSLContext
) {
  fun findByLastName(last_name: String): Author? {
    return this.dslContext.select()
      .from(AUTHOR)
      .where(AUTHOR.LAST_NAME.eq(last_name))
      .fetchOne()?.let { toModel(it) }
  }

  fun findAll(): List<Author> {
    return this.dslContext.select()
      .from(AUTHOR)
      .fetch().map { toModel(it) }
  }

  fun insert(firstName: String, lastName: String, birthYear: Int, remarks: String): Author {
    val record = this.dslContext.newRecord(AUTHOR).also {
      it.authorId = UUIDGenerator().toString()
      it.firstName = firstName
      it.lastName = lastName
      it.birthYear = birthYear
      it.remarks = remarks
      it.store()
    }
    return Author(record.authorId!!, record.firstName!!, record.lastName!!, record.birthYear!!, record.remarks!!)
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
