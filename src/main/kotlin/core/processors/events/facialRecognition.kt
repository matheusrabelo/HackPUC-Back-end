package core.processors.events

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder
import com.amazonaws.services.rekognition.model.*
import com.amazonaws.util.IOUtils
import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration
import com.amazonaws.ClientConfigurationFactory
import com.amazonaws.Protocol
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import com.typesafe.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.io.File
import java.io.FileInputStream




class facialRecognition: KodeinGlobalAware {
    val Config : Config = instance()
    val Logger : Logger = LoggerFactory.getLogger(this::class.java!!)
    var awsCred : AWSCredentials

    var similarityThreshold: Float = 70f
    var sourceImage = "source.jpg"
    var sourceImageBytes: ByteBuffer? = null
    var targetImageBytes: ByteBuffer? = null

    constructor() {
        awsCred = BasicAWSCredentials(Config.getString("amazon.accessKey"), Config.getString("amazon.accessSecret"))
    }

    fun recognize(targetImageBytes: ByteArray): Boolean {
        val a = ClientConfiguration()

        val rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withClientConfiguration(a)
                .withRegion(Regions.US_EAST_1)
                .withCredentials(AWSStaticCredentialsProvider(awsCred))
                .build()


        try {
            FileInputStream(File(sourceImage)).use { inputStream -> sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream)) }
        } catch (e: Exception) {
            System.out.println("Failed to load source image " + sourceImage)
        }

        val source = Image()
                .withBytes(sourceImageBytes)
        val target = Image()
                .withBytes(ByteBuffer.wrap(targetImageBytes))

        val request = CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold)

        // Call operation
        val compareFacesResult = rekognitionClient.compareFaces(request)


        // Display results
        val faceDetails = compareFacesResult.getFaceMatches()
        for (match in faceDetails) {
            val face = match.getFace()
            val position = face.getBoundingBox()
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + face.getConfidence()!!.toString()
                    + "% confidence.")
            if(face.getConfidence()!! >= similarityThreshold)
            {
                return true
            }
        }
        return false
    }
}
