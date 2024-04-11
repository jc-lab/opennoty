package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id

class GetPublishResponseBody(
    @Id
    var id: String,

    @JsonProperty("createdAt")
    var createdAt: Long,

    @JsonProperty("updatedAt")
    var updatedAt: Long,

    @JsonProperty("metadata")
    var metadata: Map<String, Any>,

    @JsonProperty("data")
    var data: Map<String, Any>,

    @JsonProperty("secureData")
    var secureData: String? = null,

    @JsonProperty("recipients")
    var recipients: List<Recipient>,
)