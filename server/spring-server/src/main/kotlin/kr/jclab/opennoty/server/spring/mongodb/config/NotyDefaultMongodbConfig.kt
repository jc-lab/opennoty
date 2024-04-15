package kr.jclab.opennoty.server.spring.mongodb.config

import kr.jclab.opennoty.server.spring.mongodb.NotyMethodConverter
import kr.jclab.opennoty.server.spring.mongodb.repository.NotificationRepository
import kr.jclab.opennoty.server.spring.mongodb.repository.NotyDefaultMongodbEntityRepository
import kr.jclab.opennoty.server.spring.mongodb.repository.PublishRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Import(NotyDefaultMongodbRepositoryConfig::class)
open class NotyDefaultMongodbConfig {
    @Bean
    @ConditionalOnMissingBean(MongoCustomConversions::class)
    open fun notyCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(listOf(
            NotyMethodConverter.ToStringConverter(),
            NotyMethodConverter.FromStringConverter(),
        ))
    }

    @Bean
    open fun notyDefaultMongodbEntityRepository(
        publishRepository: PublishRepository,
        notificationRepository: NotificationRepository,
        mongoTransactionManager: MongoTransactionManager,
    ): NotyDefaultMongodbEntityRepository {
        return NotyDefaultMongodbEntityRepository(
            publishRepository,
            notificationRepository,
            mongoTransactionManager,
        )
    }
}