package kr.jclab.opennoty.server.provider

import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.entity.Publish

/**
 * you can i18n
 */
@FunctionalInterface
interface NotificationDataFilterPerUser {
    fun filter(notification: Notification, publish: Publish): Map<String, Any>
}