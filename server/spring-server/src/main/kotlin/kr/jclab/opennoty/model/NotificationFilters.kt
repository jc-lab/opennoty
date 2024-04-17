package kr.jclab.opennoty.model

class NotificationFilters(
    val metadata: Map<String, Any>? = null,
    val flags: List<FlagFilter>? = null,
    val data: Map<String, Any>? = null,
)