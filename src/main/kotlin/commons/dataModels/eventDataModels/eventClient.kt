package commons.dataModels.eventDataModels

import javax.persistence.Entity


@Entity
data class eventClient(val id: Int, val idClient: Int, val idEvent: Int)