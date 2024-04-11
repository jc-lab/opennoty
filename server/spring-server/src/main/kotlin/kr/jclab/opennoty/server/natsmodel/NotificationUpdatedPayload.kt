package kr.jclab.opennoty.server.natsmodel

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class NotificationUpdatedPayload(
    @JsonProperty("notificationId") var notificationId: String,
    @JsonProperty("consumableData") var consumableData: Map<String, Any>?,
)