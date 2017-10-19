package core.processors.events

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import com.typesafe.config.Config
import core.controllers.dataContracts.*
import infrastructure.database.hackDatabaseProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class hackProcessor : KodeinGlobalAware {
    val Config : Config = instance()
    val Logger : Logger = LoggerFactory.getLogger(this::class.java!!)
    val DatabaseProvider : hackDatabaseProvider = instance()

    fun makePayment(payment: paymentPost){
        DatabaseProvider.createPayment(payment)
    }

    fun getClientPaymentResume(checkoutRequest: checkoutRequest) : Float{
        val clientEventPaymentResume : Float = DatabaseProvider.getClientEventPaymentResume(checkoutRequest)
        return clientEventPaymentResume
    }

    fun credEventClient(credRequest: credRequest) : Int{
        val eventCliendId : Int = DatabaseProvider.insertEventClient(credRequest)
        return eventCliendId
    }

    fun addClient(clientRequest: clientRequest) : Int{
        val cliendId : Int = DatabaseProvider.insertClient(clientRequest)
        return cliendId
    }

    fun addEvent(eventRequest: eventRequest) : Int{
        val eventId : Int = DatabaseProvider.insertEvent(eventRequest)
        return eventId
    }

    fun getDashboardInfo(request: dashboardRequest): dashboardResponse{
        val visitors = DatabaseProvider.getVisitorsCount(request.eventId)
        val sales = DatabaseProvider.getSalesCount(request.eventId)
        val payments = DatabaseProvider.getPaymentCount(request.eventId)
        val revenue = DatabaseProvider.getRevenue(request.eventId)
        val paymentEventList = DatabaseProvider.getPaymentEventList(request.eventId)
        val credEventList = DatabaseProvider.getCredEventList(request.eventId)
        val checkoutEventList = DatabaseProvider.getCheckoutList(request.eventId)

        return dashboardResponse(visitors, sales, payments, revenue, paymentEventList, credEventList, checkoutEventList)
    }
}