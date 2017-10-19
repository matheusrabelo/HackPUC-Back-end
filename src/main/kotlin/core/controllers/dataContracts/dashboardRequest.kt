package core.controllers.dataContracts

import javax.persistence.Entity

@Entity
data class dashboardRequest(val eventId: Int)