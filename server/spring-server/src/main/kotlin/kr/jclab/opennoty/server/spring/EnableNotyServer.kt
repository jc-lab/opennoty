package kr.jclab.opennoty.server.spring

import io.nats.spring.boot.autoconfigure.NatsAutoConfiguration
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration
import org.springframework.context.annotation.Import

@Retention
@Target(AnnotationTarget.CLASS)
@Import(
    NatsAutoConfiguration::class,
    NotyServerBuilderConfiguration::class,
    NotyServerConfiguration::class,
    GraphQlAutoConfiguration::class,
)
annotation class EnableNotyServer