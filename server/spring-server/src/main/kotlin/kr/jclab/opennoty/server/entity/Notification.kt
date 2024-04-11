package kr.jclab.opennoty.server.entity

import kr.jclab.opennoty.model.Recipient

/**
 * In-App notification
 */
interface Notification : UserAwareEntity {
    /**
     * millisecond
     */
    val timestamp: Long
    val notificationId: String
    val publishId: String
    var sent: Boolean
    var readMarked: Boolean
    var recipient: Recipient
}