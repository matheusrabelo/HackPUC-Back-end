package infrastructure.database

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import com.typesafe.config.Config
import commons.dataModels.eventDataModels.checkoutEvent
import commons.dataModels.eventDataModels.credEvent
import commons.dataModels.eventDataModels.paymentEvents
import core.controllers.dataContracts.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class hackDatabaseProvider : KodeinGlobalAware {
    val Config: Config = instance()
    val Logger: Logger = LoggerFactory.getLogger(this::class.java!!)

    fun getAllEventGuid() {
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select * from event")

        conn.close()
        return
    }

    fun createPayment(payment: paymentPost) {
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        st.execute("insert into payment(id_event_client, value) select " +
                " a.id, ${payment.value}" +
                " from event_client a" +
                " where a.id_client = ${payment.clientId} and a.id_event = ${payment.eventId}")

        conn.close()
    }

    fun getClientEventPaymentResume(checkoutRequest: checkoutRequest) : Float{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select id_event_client, sum(value) as value from payment pay inner join event_client ec on pay.id_event_client = ec.id where ec.id_client = ${checkoutRequest.clientId} and ec.id_event = ${checkoutRequest.eventId} group by id_event_client")
        val parsedResult = parseResultSetToPaymentResume(result)
        st.execute("insert into public.checkout(id_event_client,value) values (${parsedResult.first},${parsedResult.second})")

        conn.close()
        return parsedResult.second
    }

    fun getVisitorsCount(eventId: Int) : Int{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select count(*) as count from event_client where id_event = ${eventId}")
        val parsedResult = parseResultSetCount(result)

        conn.close()
        return parsedResult
    }

    fun getSalesCount(eventId: Int) : Int{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select count(*) as count from payment pay inner join event_client ec on pay.id_event_client = ec.id where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetCount(result)

        conn.close()
        return parsedResult
    }

    fun getPaymentCount(eventId: Int): Int {
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select count(*) as count from checkout pay inner join event_client ec on pay.id_event_client = ec.id where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetCount(result)

        conn.close()
        return parsedResult
    }

    fun getRevenue(eventId: Int): Float {
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select sum(pay.value) as value from checkout pay inner join event_client ec on pay.id_event_client = ec.id where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetValue(result)

        conn.close()
        return parsedResult
    }

    fun getPaymentEventList(eventId: Int): Collection<paymentEvents> {
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select c.name, pay.value, pay.created_at from payment pay" +
                " inner join event_client ec on pay.id_event_client = ec.id" +
                " inner join client c on ec.id_client = c.id" +
                " where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetPaymentEvent(result)

        conn.close()
        return parsedResult
    }

    fun getCredEventList(eventId: Int): Collection<credEvent>{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select c.name, ec.created_at" +
                " from event_client ec" +
                " inner join client c on ec.id_client = c.id" +
                " where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetCredEvent(result)

        conn.close()
        return parsedResult
    }

    fun getCheckoutList(eventId: Int): Collection<checkoutEvent>{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        val result = st.executeQuery("select c.name, ck.value, ck.created_at" +
                " from event_client ec" +
                " inner join checkout ck on ec.id = ck.id_event_client" +
                " inner join client c on ec.id_client = c.id" +
                " where ec.id_event = ${eventId}")
        val parsedResult = parseResultSetCheckoutEvent(result)

        conn.close()
        return parsedResult
    }

    fun insertEventClient(credRequest: credRequest) : Int{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        st.execute("insert into public.event_client(id_client, id_event) values (${credRequest.clientId}, ${credRequest.eventId})")
        val result = st.executeQuery("select id from event_client ec where ec.id_client = ${credRequest.clientId} and ec.id_event = ${credRequest.eventId} limit 1")
        val parsedResult = parseResultSetId(result)

        conn.close()
        return parsedResult
    }

    fun insertClient(clientRequest: clientRequest) : Int{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        st.execute("insert into public.client(name) values ('${clientRequest.name}')")
        val result = st.executeQuery("select id from client where name = '${clientRequest.name}' order by id desc")
        val parsedResult = parseResultSetId(result)

        conn.close()
        return parsedResult
    }

    fun insertEvent(eventRequest: eventRequest) : Int{
        var conn: Connection = DriverManager.getConnection(Config.getString("database.url"))
        val st = conn.createStatement()
        st.execute("insert into public.event(name) values ('${eventRequest.name}')")
        val result = st.executeQuery("select id from event where name = '${eventRequest.name}' limit 1")
        val parsedResult = parseResultSetId(result)

        conn.close()
        return parsedResult
    }

    private fun parseResultSetToPaymentResume(result: ResultSet): Pair<Int,Float> {
        var parsedResult = mutableListOf<Pair<out Int,out Float>>()
        while(result.next()) {
            val idEventClient = result.getInt("id_event_client")
            val value = result.getFloat("value")
            var pairResult = Pair(idEventClient, value)
            parsedResult.add(pairResult)
        }
        return parsedResult.first()
    }

    private fun parseResultSetId(result: ResultSet): Int {
        var parsedResult = mutableListOf<Int>()
        while(result.next()) {
            val id = result.getInt("id")
            parsedResult.add(id)
        }
        return parsedResult.first()
    }

    private fun parseResultSetCount(result: ResultSet): Int {
        var parsedResult = mutableListOf<Int>()
        while(result.next()) {
            val id = result.getInt("count")
            parsedResult.add(id)
        }
        return parsedResult.first()
    }

    private fun parseResultSetPaymentEvent(result: ResultSet): Collection<paymentEvents> {
        var parsedResult = mutableListOf<paymentEvents>()
        while(result.next()) {
            val name = result.getString("name")
            val value = result.getFloat("value")
            val created_at = result.getString("created_at")

            val result = paymentEvents(name, value, created_at)
            parsedResult.add(result)
        }
        return parsedResult
    }

    private fun parseResultSetValue(result: ResultSet): Float {
        var parsedResult = mutableListOf<Float>()
        while(result.next()) {
            val value = result.getFloat("value")

            parsedResult.add(value)
        }
        return parsedResult.first()
    }

    private fun parseResultSetCredEvent(result: ResultSet): Collection<credEvent> {
        var parsedResult = mutableListOf<credEvent>()
        while(result.next()) {
            val name = result.getString("name")
            val created_at = result.getString("created_at")

            val result = credEvent(name, created_at)
            parsedResult.add(result)
        }
        return parsedResult
    }

    private fun parseResultSetCheckoutEvent(result: ResultSet): Collection<checkoutEvent> {
        var parsedResult = mutableListOf<checkoutEvent>()
        while(result.next()) {
            val name = result.getString("name")
            val value = result.getFloat("value")
            val created_at = result.getString("created_at")

            val result = checkoutEvent(name, value, created_at)
            parsedResult.add(result)
        }
        return parsedResult
    }
}