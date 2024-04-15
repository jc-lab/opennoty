package kr.jclab.opennoty.server.spring.mongodb.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
open class NotyAutoConfigureConfig {

    @Configuration
    @ConditionalOnProperty(name = ["noty.server.database"], havingValue = "mongodb", matchIfMissing = false)
    open class NotyDefaultMongodbConfigImpl : NotyDefaultMongodbConfig()

}