package core.controllers.dataContracts

import javax.persistence.Entity

@Entity
data class checkoutRequest(val clientId: Int, val eventId: Int)