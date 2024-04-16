package kr.jclab.opennoty.server.spring.service

import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.model.NotyMethod
import kr.jclab.opennoty.server.authentication.UserAuthentication
import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.spring.NotyServer
import org.springframework.stereotype.Service

@Service
class NotyNotificationService(
    private val notyServer: NotyServer,
) {
    fun getPagedNotifications(
        userAuthentication: UserAuthentication,
        method: List<NotyMethod>,
        filters: List<FilterGQL>?,
        dataFilters: Map<String, Any>?,
        pageSize: Int,
        pageNumber: Int,
    ): NotificationsResultGQL {
        val result = notyServer.entityRepository.getPagedNotifications(userAuthentication, method.map { it.value }, filters, dataFilters, pageSize, pageNumber)
        return NotificationsResultGQL(
            totalCount = result.totalCount,
            items = result.items.map {
                val data = notyServer.notificationDataFilterPerUser(it, it.publish)
                NotificationGQL.fromNotificationWithData(it, it.publish, data)
            }
        )
    }

    fun markRead(
        userAuthentication: UserAuthentication,
        notificationId: String,
        markRead: Boolean,
    ): Notification? {
        val notification = notyServer.entityRepository.getNotification(
            userAuthentication.tenantId,
            notificationId
        )
            ?.takeIf { it.userId == userAuthentication.userId }
            ?: return null

        notification.readMarked = markRead

        return notyServer.entityRepository.saveNotification(notification)
    }
}