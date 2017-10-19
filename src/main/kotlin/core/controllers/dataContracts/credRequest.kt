package core.controllers.dataContracts

import javax.persistence.Entity

@Entity
data class credRequest(val clientId: Int, val eventId : Int)