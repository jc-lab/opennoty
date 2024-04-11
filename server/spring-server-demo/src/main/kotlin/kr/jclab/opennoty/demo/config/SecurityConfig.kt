package kr.jclab.opennoty.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf { it.disable() }
        http.httpBasic { it.disable() }
        http.formLogin { it.disable() }
        http.headers { it.disable() }
        http.authorizeExchange { it.anyExchange().permitAll() }
        return http.build()
    }
}