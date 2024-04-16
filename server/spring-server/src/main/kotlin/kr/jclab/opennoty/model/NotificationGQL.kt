package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kr.jclab.opennoty.server.entity.Notification
import kr.jclab.opennoty.server.entity.NotificationWithData
import kr.jclab.opennoty.server.entity.Publish

@JsonIgnoreProperties(ignoreUnknown = true)
class NotificationGQL(
    /**
     * Notification ID
     */
    @JsonProperty("id")
    var id: String,
    @JsonProperty("readMarked")
    var readMarked: Boolean,
    @JsonProperty("data")
    var data: Map<String, Any>,
    @JsonProperty("consumableData")
    var consumableData: Map<String, Any>?,
    @JsonProperty("secureData")
    var secureData: String?,
) {
    companion object {
        fun fromNotificationWithData(notification: Notification, publish: Publish, data: Map<String, Any>, consumableData: Map<String, Any>? = null): NotificationGQL {
            return NotificationGQL(
                id = notification.notificationId,
                readMarked = notification.readMarked,
                data = data,
                secureData = publish.secureData,
                consumableData = consumableData,
            )
        }
    }
}