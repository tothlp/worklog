package hu.tothlp.worklog.command

import hu.tothlp.worklog.command.Files.getFileFromUserDir
import hu.tothlp.worklog.command.Files.readLines
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath

@Serializable
data class Config private constructor(
	val showTimestamps: Boolean = false,
	private val dataFile: String? = null,
) {
	val dataFilePath get() = dataFile?.let { it.toPath() } ?: getFileFromUserDir(DEFAULT_DATABASE_FILE)

	companion object {
		fun load(): Config {
			val configFile = getFileFromUserDir(DEFAULT_CONFIG_FILE)?.takeIf { FileSystem.SYSTEM.exists(it) }
			val config = configFile?.readLines()?.joinToString(separator = "")?.let {
				Json(Json.Default) { ignoreUnknownKeys = true }.decodeFromString<Config>(it)
			}
				?: Config()
			return config
		}

	}
}