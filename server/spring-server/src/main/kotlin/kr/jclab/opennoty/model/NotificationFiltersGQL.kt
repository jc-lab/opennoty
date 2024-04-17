package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class NotificationFiltersGQL(
    @JsonProperty("flags")
    var flags: List<FlagFilter>? = null,
    @JsonProperty("data")
    var data: Map<String, Any>? = null,
)