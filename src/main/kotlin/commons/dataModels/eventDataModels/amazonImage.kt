package commons.dataModels.eventDataModels

import javax.persistence.Entity

@Entity
data class amazonImage(val Bytes: String, val S3Object : s3Object)
