package saurus.plesio.bookserver

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

@Suppress("unused")
class ProjectConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(SpringExtension)
}