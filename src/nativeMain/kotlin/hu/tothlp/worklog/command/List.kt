package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import hu.tothlp.worklog.dto.Log
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import okio.*
import okio.ByteString.Companion.decodeBase64
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

class List: CliktCommand() {
	val all by option().flag()
	override fun run() {
		val file = getDefaultFile().toPath()
		val lines = readLines(file)
		val logs = lines.map {
			val hex = it.decodeBase64()!!.utf8()
			Json.decodeFromString<Log>(hex)
		}.filter { all || Clock.System.now().toLocalDateTime().date == it.timestamp!!.date }
		// convert with local timezone
		logs.forEach {
			echo(it)
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