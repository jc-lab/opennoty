package kr.jclab.opennoty.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class NotyMethod(val value: String) {
    @JsonProperty("notification")
    NOTIFICATION("notification"),
    @JsonProperty("email")
    EMAIL("email"),;

    companion object {
        private val MAP_BY_VALUE = values().associateBy { it.value }

        fun findByValue(value: String): NotyMethod? = MAP_BY_VALUE[value]
    }
}
