package saurus.plesio.bookserver.controller.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import saurus.plesio.bookserver.db.AuthorRepository


@RestController
class AuthorController {
  @Autowired
  lateinit var authorRepository: AuthorRepository

  @GetMapping("/get-author/all")
  fun getGreetingByQuery() = authorRepository.findAll()
}

