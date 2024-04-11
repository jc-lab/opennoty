package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class PublishUpdateRequestBody(
    /**
     * if data is null, not update
     */
    @JsonProperty("metadata")
    var metadata: Map<String, Any>? = null,

    /**
     * if data is null, not update
     */
    @JsonProperty("data")
    var data: Map<String, Any>? = null,

    @JsonProperty("consumableData")
    var consumableData: Map<String, Any>? = null,

    @JsonProperty("secureData")
    var secureData: Map<String, Any>? = null,
)