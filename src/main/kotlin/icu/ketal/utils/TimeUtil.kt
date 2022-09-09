package icu.ketal.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

val timeZone = TimeZone.currentSystemDefault()

val Clock.now get() = this.now().toLocalDateTime(timeZone)

val Instant.localDate: LocalDate
    get() = this.toLocalDateTime(timeZone).date

class LocalDateProgression(
    private val start: LocalDate,
    private val endInclusive: LocalDate,
    private val step: Int = 1
) : Iterable<LocalDate> {
    init {
        require(step != 0) { "Step must be non-zero." }
        require(step != Int.MIN_VALUE) { "Step must be greater than Int.MIN_VALUE to avoid overflow on negation." }
        require(step == 1) { "Custom step are not currently supported" }
    }

    override fun iterator(): LocalDateProgressionIterator {
        return LocalDateProgressionIterator(start, endInclusive, step)
    }
}

class LocalDateProgressionIterator(
    first: LocalDate,
    last: LocalDate,
    private val step: Int
) : Iterator<LocalDate> {
    private val finalElement: LocalDate = last
    private var hasNext: Boolean = if (step > 0) first <= last else first >= last
    private var next: LocalDate = if (hasNext) first else finalElement
    override fun hasNext(): Boolean = hasNext

    override fun next(): LocalDate {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next = next.plus(step, DateTimeUnit.DAY)
        }
        return value
    }
}
