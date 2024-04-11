package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.server.spring.mongodb.dto.NotificationDTO
import org.bson.types.ObjectId

interface NotificationCustomRepository {
    fun findNotificationWithData(tenantId: String, notificationId: ObjectId): NotificationDTO?
    fun getPagedNotifications(tenantId: String, userId: String, method: List<String>, filters: List<FilterGQL>?, dataFilters: Map<String, Any>?, pageSize: Int, pageNumber: Int): NotificationsResultGQL
}