package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.server.spring.mongodb.entity.PublishEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PublishRepository : MongoRepository<PublishEntity, String> {
    fun findByTenantIdAndId(tenantId: String, id: String): Optional<PublishEntity>
}