package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonProperty

open class BasicResponseBody(
    @JsonProperty("message") var message: String?,
)