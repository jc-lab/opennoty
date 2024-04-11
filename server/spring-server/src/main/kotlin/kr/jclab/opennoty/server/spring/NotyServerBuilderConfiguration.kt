package kr.jclab.opennoty.server.spring

import io.nats.client.Connection
import kr.jclab.opennoty.server.entity.EntityRepository
import kr.jclab.opennoty.server.spring.config.NotyServerProperties
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportAware
import org.springframework.core.type.AnnotationMetadata
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NotyServerProperties::class)
open class NotyServerBuilderConfiguration : ImportAware, BeanClassLoaderAware {
    private lateinit var beanClassLoader: ClassLoader

    override fun setImportMetadata(importMetadata: AnnotationMetadata) {
        val enableWebSecurityAttrMap = importMetadata
            .getAnnotationAttributes(EnableNotyServer::class.java.name)
//        val enableWebSecurityAttrs = AnnotationAttributes.fromMap(enableWebSecurityAttrMap)
//        this.debugEnabled = !!.getBoolean("debug")
//        if (this.webSecurity != null) {
//            this.webSecurity.debug(this.debugEnabled)
//        }
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.beanClassLoader = classLoader
    }

    @Bean
    open fun notyServerBuilder(
        @Autowired(required = false) natsConnection: Connection?,
        @Autowired(required = false) entityRepository: EntityRepository?,
        @Autowired(required = false) javaMailSender: JavaMailSender?
    ): NotyServerBuilder {
        return NotyServerBuilder(
            natsConnection = natsConnection,
            entityRepository = entityRepository,
            javaMailSender = javaMailSender,
        )
    }
}