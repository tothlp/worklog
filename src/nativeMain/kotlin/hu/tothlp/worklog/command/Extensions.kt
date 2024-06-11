package hu.tothlp.worklog.command

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalDateTime(): LocalDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())