package saurus.plesio.bookserver.util

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val customObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule()) // JSR-310 サポート