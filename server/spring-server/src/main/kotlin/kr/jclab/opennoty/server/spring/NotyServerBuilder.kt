package kr.jclab.opennoty.server.spring

import io.nats.client.Connection
import kr.jclab.opennoty.server.entity.EntityRepository
import org.springframework.mail.javamail.JavaMailSender

class NotyServerBuilder(
    var natsConnection: Connection?,
    var entityRepository: EntityRepository?,
    var javaMailSender: JavaMailSender?,
) {
    fun entityFactory(entityRepository: EntityRepository): NotyServerBuilder {
        this.entityRepository = entityRepository
        return this
    }

    fun natsConnection(natsConnection: Connection): NotyServerBuilder {
        this.natsConnection = natsConnection
        return this
    }

    fun build(): NotyServer {
        return NotyServer(
            natsConnection = checkNotNull(natsConnection),
            entityRepository = checkNotNull(entityRepository),
            javaMailSender = javaMailSender,
        )
    }
}