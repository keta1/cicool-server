package icu.ketal.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val timeZone = TimeZone.currentSystemDefault()

val Clock.now get() = this.now().toLocalDateTime(timeZone)
