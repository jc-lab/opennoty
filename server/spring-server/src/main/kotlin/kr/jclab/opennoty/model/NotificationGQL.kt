package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kr.jclab.opennoty.server.entity.NotificationWithData

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
        fun fromNotificationWithData(input: NotificationWithData, consumableData: Map<String, Any>? = null): NotificationGQL {
            return NotificationGQL(
                id = input.notificationId,
                readMarked = input.readMarked,
                data = input.publish.data,
                secureData = input.publish.secureData,
                consumableData = consumableData,
            )
        }
    }
}