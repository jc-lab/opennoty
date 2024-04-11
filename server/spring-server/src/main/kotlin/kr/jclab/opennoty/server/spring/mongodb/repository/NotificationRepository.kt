package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.server.spring.mongodb.entity.NotificationEntity
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : MongoRepository<NotificationEntity, ObjectId>, NotificationCustomRepository {
    fun findByTenantIdAndId(tenantId: String, id: ObjectId): Optional<NotificationEntity>
    fun findAllByTenantIdAndPublishId(tenantId: String, publishId: String): List<NotificationEntity>
}