package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.model.NotificationFilters
import kr.jclab.opennoty.server.entity.NotificationsResult
import kr.jclab.opennoty.server.spring.mongodb.dto.NotificationDTO
import org.bson.types.ObjectId

interface NotificationCustomRepository {
    fun findNotificationWithData(tenantId: String, notificationId: ObjectId): NotificationDTO?
    fun getPagedNotifications(tenantId: String, userId: String, method: List<String>, filters: NotificationFilters?, pageSize: Int, pageNumber: Int): NotificationsResult
}