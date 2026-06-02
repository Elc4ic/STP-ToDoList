package dev.stp.app.data.mapper

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateFormater {
    fun formatDateFromMillis(millis: Long): String {
        val date = Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        return date.format(formatter)
    }
}
