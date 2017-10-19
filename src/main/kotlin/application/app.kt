package application

import application.configuration.dependencyInjector
import com.github.salomonbrys.kodein.conf.*
import com.github.salomonbrys.kodein.instance
import com.typesafe.config.Config
import core.controllers.hackController
import org.jooby.*
import org.jooby.handlers.Cors
import org.jooby.handlers.CorsHandler
import org.jooby.swagger.SwaggerUI
import org.jooby.json.Jackson

object app : KodeinGlobalAware
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        dependencyInjector().Setup()
        Class.forName("org.postgresql.Driver")
        val config : Config = instance()

        run("org.eclipse.jetty.server.Request.maxFormContentSize=99900000") {
            SwaggerUI().install(this)
            use("*", CorsHandler(Cors()))
            use(Jackson())

            use(hackController::class)
        }
    }
}
