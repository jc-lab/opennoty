package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class PublishRequestBody(
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

    /**
     * Public key for encrypting secure data.
     * X25519 JWK
     */
    @JsonProperty("publicKey")
    var publicKey: Map<String, Any>? = null,

    @JsonProperty("secureData")
    var secureData: Map<String, Any>? = null,

    /**
     * recipient user ids
     */
    @JsonProperty("recipients")
    var recipients: List<Recipient>,
)