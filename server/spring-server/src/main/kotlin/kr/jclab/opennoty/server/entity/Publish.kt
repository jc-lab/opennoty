package kr.jclab.opennoty.server.entity

import kr.jclab.opennoty.model.Recipient

interface Publish : TenantAwareEntity {
    val id: String

    /**
     * milliseconds
     */
    val createdAt: Long

    /**
     * milliseconds
     */
    val updatedAt: Long

    var data: Map<String, Any>

    var metadata: Map<String, Any>

    val publicKey: Map<String, Any>?

    var secureData: String?

    var recipients: List<Recipient>
}
