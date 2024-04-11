package kr.jclab.opennoty.server.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.nats.client.Connection
import io.nats.client.impl.Headers
import kr.jclab.opennoty.model.NatsConstants
import kr.jclab.opennoty.server.entity.EntityRepository
import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.entity.Publish
import kr.jclab.opennoty.server.natsmodel.NotificationUpdatedPayload
import org.springframework.mail.javamail.JavaMailSender

class NotyServer(
    val natsConnection: Connection,
    val entityRepository: EntityRepository,
    val javaMailSender: JavaMailSender?,
) {
    val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun notificationUpdated(publish: Publish, notifications: List<Notification>, consumableData: Map<String, Any>?) {
        val headers = Headers()
        headers.add(NatsConstants.HEADER_NAME_TENANT_ID, publish.tenantId)
        notifications.forEach { item ->
            val payload = NotificationUpdatedPayload(
                notificationId = item.notificationId,
                consumableData = consumableData,
            )
            natsConnection.publish(getNatsUserIdSubjectName(publish.tenantId, item.userId, "noti"), headers, objectMapper.writeValueAsBytes(payload))
        }
    }

    fun getNatsUserIdSubjectName(tenantId: String, userId: String, type: String): String {
        return "noty.${tenantId}.user.${userId}.${type}"
    }
}