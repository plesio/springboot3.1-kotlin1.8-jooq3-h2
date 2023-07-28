package saurus.plesio.bookserver.model

data class Author(
  // PK
  val authorId: String,
  // Other
  val authorName: String, val birthYear: Int? = null, val remarks: String? = null
)