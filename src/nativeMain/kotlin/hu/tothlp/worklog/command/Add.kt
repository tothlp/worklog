package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import hu.tothlp.worklog.dto.Log
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Add: CliktCommand(
	name = "add",
	help = "Add a new task"
) {
	private val task by argument(help = "Name of the task")
	private val timestamp by option("-t", "--timestamp",help = "Overrides the timestamp of the task, eg.: 2024-01-01T13:01:00").convert("timestamp") { parseDate(it) ?: fail("Invalid date") }

	override fun run() {
		val log = Log(task, timestamp)
		val logJson = log.base64Format()
		val file = getDefaultFile().toPath()
		writeEnv(file, logJson)
	}

	fun parseDate(date: String) = runCatching { LocalDateTime.parse(date) }.getOrNull()

	@OptIn(ExperimentalEncodingApi::class)
	fun Log.base64Format() = Json.encodeToString(this).encodeToByteArray().let { Base64.encode(it) }

	fun writeEnv(path: Path, data: String) {
		FileSystem.SYSTEM.appendingSink(path, false).buffer().use {
				it.writeUtf8(data)
				it.writeUtf8("\n")
		}
	}

	@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
	private fun getDefaultFile(): String = when (Platform.osFamily) {
		OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\worklog.json")
		else -> getenv("HOME")?.toKString()?.plus("/worklog.json")
	}.orEmpty()
}