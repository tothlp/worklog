package hu.tothlp.worklog.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class Worklog: CliktCommand() {

	init {
		subcommands(registerSubcommands())
	}

	private fun registerSubcommands() = listOf(
		Add(),
		List()
	)

	override fun run() {

	}


}