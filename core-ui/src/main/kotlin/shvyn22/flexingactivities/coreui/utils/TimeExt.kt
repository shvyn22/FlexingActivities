package shvyn22.flexingactivities.coreui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class DatePattern(val pattern: String) {
    ABBREVIATED_WEEKDAY("EEE"),
    DAY_MONTH("d MMM"),
    FULL_DATE("d MMM yyyy, HH:mm"),
}

fun LocalDate.format(datePattern: DatePattern): String {
    return format(DateTimeFormatter.ofPattern(datePattern.pattern))
}

fun LocalDateTime.format(datePattern: DatePattern): String {
    return format(DateTimeFormatter.ofPattern(datePattern.pattern))
}

fun Long.toFormattedDateTime(): String {
    val dateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return dateTime.format(DatePattern.FULL_DATE)
}