package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.model.Recipient
import kr.jclab.opennoty.server.authentication.UserAuthentication
import kr.jclab.opennoty.server.entity.EntityRepository
import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.entity.NotificationWithData
import kr.jclab.opennoty.server.entity.Publish
import kr.jclab.opennoty.server.spring.mongodb.entity.NotificationEntity
import kr.jclab.opennoty.server.spring.mongodb.entity.PublishEntity
import org.bson.types.ObjectId
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import java.util.function.Function

class NotyDefaultMongodbEntityRepository(
    private val publishRepository: PublishRepository,
    private val notificationRepository: NotificationRepository,
    private val mongoTransactionManager: MongoTransactionManager,
) : EntityRepository {
    override fun createPublish(
        tenantId: String,
        id: String,
        metadata: Map<String, Any>,
        data: Map<String, Any>,
        publicKey: Map<String, Any>?,
        secureData: String?,
        recipients: List<Recipient>,
    ): Publish {
        val now = System.currentTimeMillis()
        return publishRepository.save(
            PublishEntity(
                tenantId = tenantId,
                id = id,
                createdAt = now,
                updatedAt = now,
                metadata = metadata,
                data = data,
                publicKey = publicKey,
                secureData = secureData,
                recipients = recipients,
            )
        )
    }

    override fun createNotifications(publish: Publish, recipients: Collection<Recipient>): List<Notification> {
        val entities = recipients.map {
            NotificationEntity(
                tenantId = publish.tenantId,
                publishId = publish.id,
                id = ObjectId.get(),
                recipient = it,
            )
        }
        return notificationRepository.saveAll(entities)
    }

    override fun getPublish(tenantId: String, publishId: String): Publish? {
        return publishRepository.findByTenantIdAndId(tenantId, publishId).orElse(null)
    }

    override fun getNotification(tenantId: String, notificationId: String): Notification? {
        return notificationRepository.findByTenantIdAndId(tenantId, ObjectId(notificationId)).orElse(null)
    }

    override fun getNotificationWithData(tenantId: String, notificationId: String): NotificationWithData? {
        return notificationRepository.findNotificationWithData(tenantId, ObjectId(notificationId))
    }

    override fun getPagedNotifications(userAuthentication: UserAuthentication, method: List<String>, filters: List<FilterGQL>?, pageSize: Int, pageNumber: Int): NotificationsResultGQL {
        return notificationRepository.getPagedNotifications(
            userAuthentication.tenantId,
            userAuthentication.userId,
            method,
            filters,
            pageSize,
            pageNumber
        )
    }

    override fun savePublish(publish: Publish): Publish {
        publish as PublishEntity
        publish.updatedAt = System.currentTimeMillis()
        return publishRepository.save(publish)
    }

    override fun saveNotification(notification: Notification): Notification {
        notification as NotificationEntity
        return notificationRepository.save(notification)
    }

    override fun getNotificationsByPublish(publish: Publish): List<Notification> {
        return notificationRepository.findAllByTenantIdAndPublishId(publish.tenantId, publish.id)
    }

    private val transactionTemplate = TransactionTemplate(mongoTransactionManager)

    override fun <R> transactional(runnable: Function<TransactionStatus, R>): R? {
        return transactionTemplate.execute<R> { status ->
            runnable.apply(status)
        }
    }
}