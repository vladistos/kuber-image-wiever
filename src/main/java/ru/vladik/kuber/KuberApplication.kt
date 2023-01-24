package ru.vladik.kuber

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import ru.vladik.kuber.utils.EnvVars
import ru.vladik.kuber.utils.getEnvVarOrEmpty

@SpringBootApplication
class KuberApplication {
	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			val path = getEnvVarOrEmpty(EnvVars.CONTEXT_PATH.name)
			if (path.isNotEmpty()) System.setProperty("server.servlet.context-path", path)
			SpringApplication.run(KuberApplication::class.java, *args)
		}
	}
}
