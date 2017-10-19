package core.controllers

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import com.typesafe.config.Config
import core.controllers.dataContracts.*
import core.processors.events.facialRecognition
import core.processors.events.hackProcessor
import org.jooby.mvc.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.text.toByteArray
import sun.text.normalizer.UTF16
import java.util.*

@Path("/api")
class hackController : KodeinGlobalAware {
    val Config : Config = instance()
    val Logger : Logger = LoggerFactory.getLogger(this::class.java!!)
    val Processor : hackProcessor = instance()
    val facialRecog : facialRecognition = instance()

    @POST
    @Path("/payment")
    fun createPayment(@Body payment: paymentPost): String  {
        Processor.makePayment(payment)

        return "TUDO 2"
    }

    @POST
    @Path("/checkout")
    fun getCheckout(@Body checkoutRequest:checkoutRequest): Float  {
        val clientEventPaymentResume : Float = Processor.getClientPaymentResume(checkoutRequest)
        return clientEventPaymentResume
    }

    @POST
    @Path("/cred")
    fun postCred(@Body credRequest: credRequest): Int  {
        val eventCliendId : Int = Processor.credEventClient(credRequest)
        return eventCliendId
    }

    @POST
    @Path("/dashboard")
    fun getDashboardInfo(@Body request: dashboardRequest): dashboardResponse {
        return Processor.getDashboardInfo(request)
    }

    @POST
    @Path("/client")
    fun postClient(@Body clientRequest: clientRequest): Int  {
        val clientId : Int = Processor.addClient(clientRequest)
        return clientId
    }

    @POST
    @Path("/event")
    fun postEvent(@Body eventRequest: eventRequest): Int  {
        val eventId : Int = Processor.addEvent(eventRequest)
        return eventId
    }

    @POST
    @Path("/facial/recognition")
    fun getFacialRecognition(@Body targetBase: faceRecognitionRequest): Boolean {
        val parsedBase = Base64.getDecoder().decode(targetBase.baseString)

        return facialRecog.recognize(parsedBase)
    }
}