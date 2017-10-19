package core.controllers.dataContracts

import javax.persistence.Entity
import commons.dataModels.eventDataModels.faceMatches

@Entity
data class faceRecognitionResponse(val ClientId : Int, val faceMatches: faceMatches)