package saurus.plesio.bookserver.db

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import saurus.plesio.bookserver.util.initTestContainer
import saurus.plesio.bookserver.util.migrateFlywayOnBeforeSpec

@Suppress("unused")
@SpringBootTest
internal class AuthorRepositoryTest(
  private val authorRepository: AuthorRepository
) : FunSpec({
  beforeSpec {
    migrateFlywayOnBeforeSpec(container)
    authorRepository.deleteAll()
  }

  afterTest { authorRepository.deleteAll() }

  test("simple save and find") {
    val authorId = authorRepository.insert("first", 1992, "last")
    val find = authorRepository.findById(authorId)
    authorId shouldBe find?.authorId
  }

  test("simple find count one") {
    authorRepository.insert("first taro", 1992, "")
    authorRepository.listAll().size shouldBe 1
  }

}) {
  companion object {
    val container = initTestContainer()
  }
}