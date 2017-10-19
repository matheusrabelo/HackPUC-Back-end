package core.controllers.dataContracts

import javax.persistence.Entity

@Entity
data class paymentPost(val clientId: Int, val eventId: Int, val value: Float)