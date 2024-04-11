package kr.jclab.opennoty.server.spring.mongodb.config

import kr.jclab.opennoty.server.spring.mongodb.repository.NotificationRepository
import kr.jclab.opennoty.server.spring.mongodb.repository.PublishRepository
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(
    basePackageClasses = [
        PublishRepository::class,
        NotificationRepository::class,
    ],
)
open class NotyDefaultMongodbRepositoryConfig
