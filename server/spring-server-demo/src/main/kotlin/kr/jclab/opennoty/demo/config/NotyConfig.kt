package kr.jclab.opennoty.demo.config

import kr.jclab.opennoty.server.spring.EnableNotyServer
import kr.jclab.opennoty.server.spring.NotyServer
import kr.jclab.opennoty.server.spring.NotyServerBuilder
import kr.jclab.opennoty.server.spring.mongodb.config.NotyDefaultMongodbConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableNotyServer
@Import(NotyDefaultMongodbConfig::class)
class NotyConfig {
    @Bean
    fun notyServer(builder: NotyServerBuilder): NotyServer {
        return builder.build()
    }
}