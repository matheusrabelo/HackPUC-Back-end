package core.controllers.dataContracts

import javax.persistence.Entity
import commons.dataModels.eventDataModels.amazonImage

@Entity
data class faceRecognitionRequest(val baseString: String)