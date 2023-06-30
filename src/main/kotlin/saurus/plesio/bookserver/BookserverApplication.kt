package saurus.plesio.bookserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookserverApplication

fun main(args: Array<String>) {
  runApplication<BookserverApplication>(*args)
}
