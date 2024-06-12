package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import hu.tothlp.worklog.dto.Log
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeBase64
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

class List : CliktCommand(
	name = "list",
	help = "List tasks"
) {

	private val t = Terminal()
	private val all by option("-a", "--all", help = "Show all entries").flag()
	private val withTime by option("-t", "--with-time", help = "Show timestamps").flag()

	override fun run() {
		val file = getDefaultFile().toPath()
		val lines = readLines(file)
		val logs = lines.map {
			val hex = it.decodeBase64()!!.utf8()
			Json.decodeFromString<Log>(hex)
		}.filter { all || Clock.System.now().toLocalDateTime().date == it.timestamp!!.date }
			.groupBy { it.timestamp!!.date }
		printLogs(logs, t.info.ansiLevel)
	}

	fun printLogs(logs: Map<LocalDate, kotlin.collections.List<Log>>, ansiLevel: AnsiLevel) {
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

	fun readLines(path: Path) = FileSystem.SYSTEM.read(path) {
		generateSequence { readUtf8Line() }.toList()
	}

	@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
	private fun getDefaultFile(): String = when (Platform.osFamily) {
		OsFamily.WINDOWS -> getenv("USERPROFILE")?.toKString()?.plus("\\worklog.json")
		else -> getenv("HOME")?.toKString()?.plus("/worklog.json")
	}.orEmpty()
}