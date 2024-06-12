package hu.tothlp.worklog.command

import kotlinx.datetime.*

fun Instant.toLocalDateTime(): LocalDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
fun LocalTime.withoutMillis(): LocalTime = LocalTime(this.hour, this.minute, this.second)