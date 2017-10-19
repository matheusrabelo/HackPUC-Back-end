package commons.dataModels.eventDataModels

import javax.persistence.Entity

@Entity
data class faceMatches(val faceMatches: Collection<face>)