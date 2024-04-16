package kr.jclab.opennoty.server.spring

import io.nats.client.Connection
import kr.jclab.opennoty.server.entity.EntityRepository
import kr.jclab.opennoty.server.provider.NotificationDataFilterPerUser
import org.springframework.mail.javamail.JavaMailSender

class NotyServerBuilder(
    var natsConnection: Connection?,
    var entityRepository: EntityRepository?,
    var javaMailSender: JavaMailSender?,
) {
    private var notificationDataFilterPerUser: NotificationDataFilterPerUser? = null

    fun entityFactory(entityRepository: EntityRepository): NotyServerBuilder {
        this.entityRepository = entityRepository
        return this
    }

    fun natsConnection(natsConnection: Connection): NotyServerBuilder {
        this.natsConnection = natsConnection
        return this
    }

    fun notificationDataFilterPerUser(notificationDataFilterPerUser: NotificationDataFilterPerUser): NotyServerBuilder {
        this.notificationDataFilterPerUser = notificationDataFilterPerUser
        return this
    }

    fun build(): NotyServer {
        return NotyServer(
            natsConnection = checkNotNull(natsConnection),
            entityRepository = checkNotNull(entityRepository),
            javaMailSender = javaMailSender,
            notificationDataFilterPerUser = notificationDataFilterPerUser,
        )
    }
}