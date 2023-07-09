package saurus.plesio.bookserver.util

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

@Component
class CustomizedMockMvc : MockMvcBuilderCustomizer {
  override fun customize(builder: ConfigurableMockMvcBuilder<*>?) {
    builder?.alwaysDo { result -> result.response.characterEncoding = "UTF-8" }
  }
}