package commons.dataModels.eventDataModels

import javax.persistence.Entity

@Entity
data class s3Object(val Bucket: String, val Name: String, val Version: String)