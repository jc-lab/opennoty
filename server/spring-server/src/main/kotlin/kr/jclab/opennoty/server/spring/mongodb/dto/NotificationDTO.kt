package kr.jclab.opennoty.server.spring.mongodb.dto

import kr.jclab.opennoty.model.Recipient
import kr.jclab.opennoty.server.entity.NotificationWithData
import kr.jclab.opennoty.server.spring.mongodb.entity.NotificationEntity
import kr.jclab.opennoty.server.spring.mongodb.entity.PublishEntity
import org.bson.types.ObjectId

class NotificationDTO(
    id: ObjectId,
    publishId: String,
    tenantId: String,
    readMarked: Boolean,
    metadata: Map<String, Any>,
    recipient: Recipient,
    sent: Boolean,
    override var publish: PublishEntity,
) : NotificationEntity(
    id = id,
    publishId = publishId,
    tenantId = tenantId,
    readMarked = readMarked,
    metadata = metadata,
    recipient = recipient,
    sent = sent
), NotificationWithData
