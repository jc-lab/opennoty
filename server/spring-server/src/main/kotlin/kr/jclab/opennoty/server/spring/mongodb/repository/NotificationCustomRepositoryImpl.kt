package kr.jclab.opennoty.server.spring.mongodb.repository

import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.server.entity.NotificationsResult
import kr.jclab.opennoty.server.spring.mongodb.dto.NotificationDTO
import kr.jclab.opennoty.server.spring.mongodb.entity.NotificationEntity
import kr.jclab.opennoty.server.spring.mongodb.entity.PublishEntity
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo

class NotificationCustomRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : NotificationCustomRepository {
    override fun findNotificationWithData(
        tenantId: String,
        notificationId: ObjectId,
    ): NotificationDTO? {
        return mongoTemplate.aggregate(
            Aggregation.newAggregation(
                NotificationEntity::class.java,
                Aggregation.match(Criteria().andOperator(
                    Criteria("tenantId").isEqualTo(tenantId),
                    Criteria("_id").isEqualTo(notificationId),
                )),
                Aggregation.lookup(
                    PublishEntity.COLLECTION_NAME,
                    "publishId",
                    "_id",
                    "publish"
                ),
                Aggregation.unwind("publish")
            ),
            NotificationDTO::class.java,
        ).mappedResults.firstOrNull()
    }

    override fun getPagedNotifications(
        tenantId: String,
        userId: String,
        method: List<String>,
        filters: List<FilterGQL>?,
        dataFilters: Map<String, Any>?,
        pageSize: Int,
        pageNumber: Int,
    ): NotificationsResult {
        val criteriaList = ArrayList<Criteria>()
        criteriaList.add(Criteria("tenantId").isEqualTo(tenantId))
        criteriaList.add(Criteria("recipient.userId").isEqualTo(userId))
        criteriaList.add(Criteria("recipient.method").`in`(method))

        if (filters != null) {
            val readMarked = filters.contains(FilterGQL.READ_MARKED)
            val readUnmarked = filters.contains(FilterGQL.READ_UNMARKED)
            if (readMarked || readUnmarked) {
                if (readMarked != readUnmarked) {
                    criteriaList.add(Criteria("readMarked").isEqualTo(readMarked))
                }
            }

            val unsent = filters.contains(FilterGQL.UNSENT)
            if (unsent) {
                criteriaList.add(Criteria("sent").isEqualTo(false))
            }
        }

        dataFilters?.forEach { (k, v) ->
            criteriaList.add(Criteria("data.${k}").isEqualTo(v))
        }

        val basicAggregation = Aggregation.newAggregation(
            NotificationEntity::class.java,
            Aggregation.match(Criteria().andOperator(criteriaList)),
            Aggregation.sort(Sort.by("_id").descending()),
        )

        val pagedAggregation = Aggregation.newAggregation(
            NotificationEntity::class.java,
            basicAggregation.pipeline.operations
        )
        pagedAggregation.pipeline.add(Aggregation.skip(((pageNumber - 1) * pageSize).toLong()))
        pagedAggregation.pipeline.add(Aggregation.limit(pageSize.toLong()))
        pagedAggregation.pipeline.add(Aggregation.lookup(
            PublishEntity.COLLECTION_NAME,
            "publishId",
            "_id",
            "publish"
        ))
        pagedAggregation.pipeline.add(Aggregation.unwind("publish"))

        val totalAggregation = Aggregation.newAggregation(
            NotificationEntity::class.java,
            basicAggregation.pipeline.operations
        )
        totalAggregation.pipeline.add(Aggregation.count().`as`("count"))

        val countResult = mongoTemplate.aggregate(totalAggregation, CountResult::class.java)
        val pagedResult = mongoTemplate.aggregate(pagedAggregation, NotificationDTO::class.java)

        return NotificationsResult(
            totalCount = countResult.mappedResults.firstOrNull()?.count ?: 0,
            items = pagedResult.mappedResults,
        )
    }

    class CountResult(
        @Field("count") var count: Int,
    )
}
