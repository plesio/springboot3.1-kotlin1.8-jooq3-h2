package saurus.plesio.bookserver.model

import java.time.LocalDate

data class Book(
  // PK
  val bookId: String,
  // Other
  val bookTitle: String,
  val isbnCode: String? = null,
  val publishedDate: LocalDate? = null,
  val remarks: String? = null
)