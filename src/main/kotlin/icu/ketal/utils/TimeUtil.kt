package icu.ketal.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val timeZone = TimeZone.currentSystemDefault()

val Clock.now get() = this.now().toLocalDateTime(timeZone)

val Instant.localDate: LocalDate
    get() = this.toLocalDateTime(timeZone).date
