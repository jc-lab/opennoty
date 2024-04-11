package kr.jclab.opennoty.server.spring.service

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.ECDHEncrypter
import com.nimbusds.jose.jwk.ECKey
import kr.jclab.opennoty.model.PublishRequestBody
import kr.jclab.opennoty.model.PublishUpdateRequestBody
import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.entity.Publish
import kr.jclab.opennoty.server.spring.NotyServer
import org.springframework.stereotype.Service

@Service
open class NotyPublishService(
    private val notyServer: NotyServer,
) {
    fun getPublish(
        tenantId: String,
        publishId: String
    ): Publish? {
        return notyServer.entityRepository.getPublish(tenantId, publishId)
    }

    fun newPublish(
        tenantId: String,
        publishId: String,
        requestBody: PublishRequestBody,
    ): Publish {
        val secureData = requestBody.secureData?.let { secureData ->
            val publicKey = ECKey.parse(requestBody.publicKey!!)
            val header = JWEHeader(JWEAlgorithm.ECDH_ES, EncryptionMethod.A256GCM)
            val payload = Payload(secureData)
            val jweObject = JWEObject(header, payload)
            val encrypter = ECDHEncrypter(publicKey)
            jweObject.encrypt(encrypter)
            jweObject.serialize()
        }
        val result = notyServer.entityRepository.transactional {
            val publish = notyServer.entityRepository.createPublish(
                tenantId = tenantId,
                id = publishId,
                metadata = requestBody.metadata ?: emptyMap(),
                data = requestBody.data ?: emptyMap(),
                publicKey = requestBody.publicKey,
                secureData = secureData,
                recipients = requestBody.recipients,
            )

            val notifications = notyServer.entityRepository.createNotifications(
                publish,
                requestBody.recipients
            )

            // TODO: RABBITMQ
            // SEND EMAIL

            Pair(publish, notifications)
        }!!

        notyServer.notificationUpdated(result.first, result.second, requestBody.consumableData)

        return result.first
    }

    fun updatePublish(
        tenantId: String,
        publishId: String,
        requestBody: PublishUpdateRequestBody,
    ): Publish? {
        var publish = notyServer.entityRepository.getPublish(tenantId, publishId) ?: return null
        var updated: Boolean = false

        requestBody.metadata?.also {
            updated = true

            publish.metadata = it
        }
        requestBody.data?.also {
            updated = true

            publish.data = it
        }
        requestBody.secureData?.also { secureData ->
            updated = true

            val publicKey = ECKey.parse(publish.publicKey!!)
            val header = JWEHeader(JWEAlgorithm.ECDH_ES, EncryptionMethod.A256GCM)
            val payload = Payload(secureData)
            val jweObject = JWEObject(header, payload)
            val encrypter = ECDHEncrypter(publicKey)
            jweObject.encrypt(encrypter)
            publish.secureData = jweObject.serialize()
        }

        var notificationEntities: List<Notification> = emptyList()

        if (updated) {
            publish = notyServer.entityRepository.transactional {
                val updatedPublish = notyServer.entityRepository.savePublish(publish)
                notificationEntities = notyServer.entityRepository.getNotificationsByPublish(publish)
                updatedPublish
            }!!
        } else {
            notificationEntities = notyServer.entityRepository.getNotificationsByPublish(publish)
        }

        // TODO: do not query all notifications...
        notyServer.notificationUpdated(publish, notificationEntities, requestBody.consumableData)

        return publish
    }
}