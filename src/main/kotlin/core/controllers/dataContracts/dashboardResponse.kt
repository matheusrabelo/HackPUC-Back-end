package core.controllers.dataContracts

import commons.dataModels.eventDataModels.checkoutEvent
import commons.dataModels.eventDataModels.credEvent
import commons.dataModels.eventDataModels.paymentEvents

data class dashboardResponse(val visitors: Int, val sales: Int, val payments: Int, val revenue: Float,
                             val paymentEventList: Collection<paymentEvents>, val credEventList: Collection<credEvent>,
                             val checkoutEventList: Collection<checkoutEvent>)