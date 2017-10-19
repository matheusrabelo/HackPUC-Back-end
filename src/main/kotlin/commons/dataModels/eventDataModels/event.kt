package commons.dataModels.eventDataModels

import java.time.*
import javax.persistence.Entity

@Entity
data class event(val id: Int, val name: String)