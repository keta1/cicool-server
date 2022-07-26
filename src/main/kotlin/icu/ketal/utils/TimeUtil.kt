package icu.ketal.utils

import java.time.LocalDateTime
import java.time.ZoneOffset

object TimeUtil {
    val now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))
}