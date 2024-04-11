package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonProperty

class NotificationsResultGQL(
    @JsonProperty("totalCount")
    var totalCount: Int,
    @JsonProperty("items")
    var items: List<NotificationGQL>,
)