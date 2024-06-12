package hu.tothlp.worklog.command

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.DIRECTORY_SEPARATOR
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

object Files {
	val t = Terminal()
	fun Path?.readLines() = this?.takeIf { FileSystem.SYSTEM.exists(it) }?.let {
		FileSystem.SYSTEM.read(this) {
			generateSequence { readUtf8Line() }.toList()
		}
	}

	@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
	fun getFileFromUserDir(file: String): Path? {
		val userProfileEnv = if (Platform.osFamily == OsFamily.WINDOWS) "USERPROFILE" else "HOME"
		val path = getenv(userProfileEnv)?.toKString()?.plus("$DIRECTORY_SEPARATOR$file")?.toPath()
		return path.also { it ?: t.println("File not found: $it") }
	}
}