package application.configuration

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import core.processors.events.facialRecognition
import core.processors.events.hackProcessor
import infrastructure.database.hackDatabaseProvider

class dependencyInjector
{
    fun Setup()
    {
        val conf = ConfigFactory.load()

        Kodein.global.addConfig {
            //config
            bind<Config>() with singleton { conf }

            //hack
            bind<hackProcessor>() with provider { hackProcessor() }
            bind<hackDatabaseProvider>() with provider { hackDatabaseProvider() }

            //facial recognition
            bind<facialRecognition>() with provider { facialRecognition() }
        }
    }
}