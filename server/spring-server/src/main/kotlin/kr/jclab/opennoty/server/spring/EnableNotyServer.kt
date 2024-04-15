package kr.jclab.opennoty.server.spring

import io.nats.spring.boot.autoconfigure.NatsAutoConfiguration
import kr.jclab.opennoty.server.spring.mongodb.config.NotyAutoConfigureConfig
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration
import org.springframework.context.annotation.Import

@Retention
@Target(AnnotationTarget.CLASS)
@Import(
    NatsAutoConfiguration::class,
    NotyServerBuilderConfiguration::class,
    NotyServerConfiguration::class,
    NotyAutoConfigureConfig::class,
    GraphQlAutoConfiguration::class,
)
annotation class EnableNotyServer