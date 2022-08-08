package icu.ketal.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime as jtLocalDateTime
import java.time.ZoneOffset as jtZoneOffset

object TimeUtil {
    val now = jtLocalDateTime.now().toEpochSecond(jtZoneOffset.of("+8"))
}

private val timeZone = TimeZone.currentSystemDefault()

val Clock.now get() = this.now().toLocalDateTime(timeZone)
