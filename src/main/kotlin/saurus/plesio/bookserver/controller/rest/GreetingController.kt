package saurus.plesio.bookserver.controller.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

// GreetingController.kt
@RestController
class GreetingController {

  @GetMapping("/get-greeting/query")
  fun getGreetingByQuery(
    @RequestParam(
      name = "name", required = false, defaultValue = "world"
    ) name: String
  ) = GreetingResponse(
    datetime = LocalDateTime.now().toString(),
    message = "Hello, $name!"
  )
}

data class GreetingResponse(
  val datetime: String,
  val message: String,
)