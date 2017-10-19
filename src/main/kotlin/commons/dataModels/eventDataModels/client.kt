package commons.dataModels.eventDataModels

import javax.persistence.Entity

@Entity
data class client(val id: Int, val name: String)