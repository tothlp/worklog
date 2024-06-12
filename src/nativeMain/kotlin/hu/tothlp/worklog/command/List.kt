package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import hu.tothlp.worklog.command.Files.readLines
import hu.tothlp.worklog.dto.Log
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeBase64

class List(
	private val config: Config = Config.load()
) : CliktCommand(
	name = "list",
	help = "List tasks"
) {

	private val t = Terminal()
	private val all by option("-a", "--all", help = "Show all entries").flag()
	private val withTime by option("-t", "--with-time", help = "Show timestamps").flag(default = config.showTimestamps)

	override fun run() {
		val file = config.dataFilePath
		val lines = file?.readLines()
		if (lines.isNullOrEmpty()) {
			t.println("No worklogs yet!")
			return
		}
		val logs = lines?.mapNotNull {
			it.decodeBase64()?.utf8()?.let { Json.decodeFromString<Log>(it) }
		}?.filter { all || Clock.System.now().toLocalDateTime().date == it.timestamp!!.date }
			?.groupBy { it.timestamp!!.date }
		printLogs(logs, t.info.ansiLevel)
	}

	fun printLogs(logs: Map<LocalDate, kotlin.collections.List<Log>>?, ansiLevel: AnsiLevel) {
		if (logs.isNullOrEmpty()) {
			t.println("No worklogs yet!")
			return
		}
		logs.entries.forEach {
			val title =
				if (ansiLevel == AnsiLevel.NONE)
					"${it.key}"
				else
					Markdown("# ${it.key}")
			t.println(title)

			it.value.forEach { log ->
				val prefix = if (withTime) {
					val basePrefix = "${log.timestamp!!.time.withoutMillis()}:"
					when (ansiLevel) {
						AnsiLevel.NONE -> "$basePrefix "
						else -> "*$basePrefix* "
					}
				} else {
					""
				}

				val line = when (ansiLevel) {
					AnsiLevel.NONE -> "* $prefix${log.message}"
					else -> Markdown(" * $prefix${log.message}")
				}

				t.println(line)

			}
		}
	}

}