package kr.jclab.opennoty.server.spring.mongodb.entity

import kr.jclab.opennoty.model.Recipient
import kr.jclab.opennoty.server.entity.Notification
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = NotificationEntity.COLLECTION_NAME)
open class NotificationEntity(
    @Id
    var id: ObjectId,
    @Field("publishId")
    override var publishId: String,
    @Field("tenantId")
    override var tenantId: String,
    @Field("readMarked")
    override var readMarked: Boolean = false,
    @Field("metadata")
    override var metadata: Map<String, Any>,
    @Field("recipient")
    override var recipient: Recipient,
    @Field("sent")
    override var sent: Boolean = false,
) : Notification {
    companion object {
        const val COLLECTION_NAME = "noty.notifications"
    }

    override val timestamp: Long
        get() = id.timestamp.toLong() * 1000

    override val notificationId: String
        get() = id.toHexString()

    override val userId: String
        get() = recipient.userId
}
