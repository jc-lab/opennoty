package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Field

@JsonIgnoreProperties(ignoreUnknown = true)
class Recipient(
    /**
     * "notification" | "email"
     */
    @JsonProperty(value = "method", required = false)
    @Field("method")
    var method: NotyMethod,

    @JsonProperty(value = "userId", required = false)
    @Field("userId")
    var userId: String,

    /**
     * "email" 사용 시
     */
    @JsonProperty(value = "email", required = false)
    @Field("email")
    var email: String?,

    /**
     * "email" 사용 시 i18n 을 위해 사용한다.
     * ISO 639-1
     */
    @JsonProperty(value = "locale", required = false)
    @Field("locale")
    var locale: String?,
)