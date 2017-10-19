package commons.dataModels.eventDataModels

import javax.persistence.Entity

@Entity
data class checkoutEvent(val clientName: String, val transactionValue: Float, val timestamp: String)