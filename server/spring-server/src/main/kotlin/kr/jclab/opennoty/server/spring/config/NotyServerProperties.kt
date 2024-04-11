package kr.jclab.opennoty.server.spring.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "noty.server")
data class NotyServerProperties(
    var database: String = "",
    var apiContextPath: String = "/api/noty/",
    var mailFrom: String = "",
)