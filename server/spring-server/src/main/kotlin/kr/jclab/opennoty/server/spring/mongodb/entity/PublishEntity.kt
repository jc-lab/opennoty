package kr.jclab.opennoty.server.spring.mongodb.entity

import kr.jclab.opennoty.model.Recipient
import kr.jclab.opennoty.server.entity.Publish
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = PublishEntity.COLLECTION_NAME)
class PublishEntity(
    @Id
    override var id: String,

    @Field("tenantId")
    override var tenantId: String,

    @Field("createdAt")
    override var createdAt: Long,

    @Field("updatedAt")
    override var updatedAt: Long,

    @Field("metadata")
    override var metadata: Map<String, Any>,

    @Field("data")
    override var data: Map<String, Any>,

    @Field("secureData")
    override var secureData: String? = null,

    @Field("recipients")
    override var recipients: List<Recipient>,

    /**
     * JWK
     */
    @Field("publicKey")
    override var publicKey: Map<String, Any>? = null,
) : Publish {
    companion object {
        const val COLLECTION_NAME = "noty.publishes"
    }
}