package hu.tothlp.worklog.dto

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class Log(
	val message: String,
	var timestamp: LocalDateTime?
) {
	init {
		timestamp = timestamp ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
	}
}
