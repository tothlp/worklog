package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import hu.tothlp.worklog.command.Files.t
import hu.tothlp.worklog.dto.Log
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Add(
	private val config: Config = Config.load()
) : CliktCommand(
	name = "add",
	help = "Add a new task"
) {
	private val task by argument(help = "Name of the task")
	private val timestamp by option(
		"-t",
		"--timestamp",
		help = "Overrides the timestamp of the task, eg.: 2024-01-01T13:01:00"
	).convert("timestamp") { parseDate(it) ?: fail("Invalid date") }

	override fun run() {
		val log = Log(task, timestamp)
		val logJson = log.base64Format()
		val file = config.dataFilePath
		file?.let { writeEnv(file, logJson) }
	}

	private fun parseDate(date: String) = runCatching { LocalDateTime.parse(date) }.getOrNull()

	@OptIn(ExperimentalEncodingApi::class)
	fun Log.base64Format() = Json.encodeToString(this).encodeToByteArray().let { Base64.encode(it) }

	private fun writeEnv(path: Path, data: String) {
		if (!FileSystem.SYSTEM.exists(path)) runCatching {
			FileSystem.SYSTEM.createDirectories(path.parent!!)
		}.onFailure {
			t.danger("Failed to create directory: ${path.parent}. Error: ${it.message}")
			return
		}

		FileSystem.SYSTEM.appendingSink(path, false).buffer().use {
			it.writeUtf8(data)
			it.writeUtf8("\n")
		}
	}
}