package kr.jclab.opennoty.server.entity

import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.model.Recipient
import kr.jclab.opennoty.server.authentication.UserAuthentication
import org.springframework.transaction.TransactionStatus
import java.util.function.Function

interface EntityRepository {
    fun createPublish(tenantId: String, id: String, metadata: Map<String, Any>, data: Map<String, Any>, publicKey: Map<String, Any>? = null, secureData: String? = null, recipients: List<Recipient>): Publish
    fun createNotifications(publish: Publish, recipients: Collection<Recipient>): List<Notification>
    fun getPublish(tenantId: String, publishId: String): Publish?
    fun getNotification(tenantId: String, notificationId: String): Notification?
    fun getNotificationWithData(tenantId: String, notificationId: String): NotificationWithData?
    fun getPagedNotifications(userAuthentication: UserAuthentication, method: List<String>, filters: List<FilterGQL>?, dataFilters: Map<String, Any>?, pageSize: Int, pageNumber: Int): NotificationsResult
    fun savePublish(publish: Publish): Publish
    fun saveNotification(notification: Notification): Notification
    fun getNotificationsByPublish(publish: Publish): List<Notification>
    fun <R> transactional(runnable: Function<TransactionStatus, R>): R?
}
