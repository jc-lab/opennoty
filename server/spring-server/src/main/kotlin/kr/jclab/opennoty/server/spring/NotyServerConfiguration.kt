package kr.jclab.opennoty.server.spring

import graphql.execution.instrumentation.Instrumentation
import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility
import kr.jclab.opennoty.server.spring.config.NotyServerProperties
import kr.jclab.opennoty.server.spring.controller.NotyApiController
import kr.jclab.opennoty.server.spring.resolver.NotificationResolver
import kr.jclab.opennoty.server.spring.service.NotyNotificationService
import kr.jclab.opennoty.server.spring.service.NotyPublishService
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportAware
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer
import org.springframework.graphql.execution.*
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import java.util.function.Predicate
import java.util.stream.Collectors

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GraphQlProperties::class)
open class NotyServerConfiguration : ImportAware, BeanClassLoaderAware {
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
    open fun notyPublishService(
        notyServer: NotyServer,
    ): NotyPublishService {
        return NotyPublishService(
            notyServer,
        )
    }

    @Bean
    open fun notyNotificationService(
        notyServer: NotyServer,
    ): NotyNotificationService {
        return NotyNotificationService(
            notyServer,
        )
    }

    @Bean
    open fun notyApiController(
        notyPublishService: NotyPublishService,
    ): NotyApiController {
        return NotyApiController(notyPublishService)
    }

    @Bean
    open fun notificationResolver(
        notyServer: NotyServer,
        notyNotificationService: NotyNotificationService,
    ): NotificationResolver {
        return NotificationResolver(notyServer, notyNotificationService)
    }

    @Bean
    open fun notyApiRequestMappingHandlerMapping(
        notyServerProperties: NotyServerProperties,
    ): RequestMappingHandlerMapping {
        val requestMappingHandlerMapping = NotyRequestMappingHandlerMapping()
        val urlMap = HashMap<String, Predicate<Class<*>>>()
        urlMap[notyServerProperties.apiContextPath] =
            Predicate<Class<*>> { it: Class<*> -> NotyApiController::class.java.isAssignableFrom(it) }
        requestMappingHandlerMapping.order = -1
        requestMappingHandlerMapping.pathPrefixes = urlMap
        return requestMappingHandlerMapping
    }

    class NotyRequestMappingHandlerMapping : RequestMappingHandlerMapping() {
        override fun isHandler(beanType: Class<*>): Boolean {
            return NotyApiController::class.java.isAssignableFrom(beanType)
        }
    }

    @Bean
    open fun notyAnnotatedControllerConfigurer(): AnnotatedControllerConfigurer {
        return AnnotatedControllerConfigurer()
    }

    @Bean
    @ConditionalOnMissingBean(name = ["notyGraphQlSource"])
    open fun notyGraphQlSource(
        resourcePatternResolver: ResourcePatternResolver,
        exceptionResolvers: ObjectProvider<DataFetcherExceptionResolver>,
        subscriptionExceptionResolvers: ObjectProvider<SubscriptionExceptionResolver>,
        instrumentations: ObjectProvider<Instrumentation>,
//        wiringConfigurers: ObjectProvider<RuntimeWiringConfigurer>,
        sourceCustomizers: ObjectProvider<GraphQlSourceBuilderCustomizer>,
        @Qualifier("notyAnnotatedControllerConfigurer") annotatedControllerConfigurer: AnnotatedControllerConfigurer,
    ): GraphQlSource {
        val schema = GraphQlProperties.Schema().also {
            it.locations = arrayOf("classpath:/noty/graphql/")
        }

        val schemaResources: Array<Resource> = resolveSchemaResources(
            resourcePatternResolver,
            schema.locations,
            schema.fileExtensions
        )
        val builder = GraphQlSource.schemaResourceBuilder()
            .schemaResources(*schemaResources)
            .exceptionResolvers(toList<DataFetcherExceptionResolver>(exceptionResolvers))
            .subscriptionExceptionResolvers(toList<SubscriptionExceptionResolver>(subscriptionExceptionResolvers))
            .instrumentation(toList<Instrumentation>(instrumentations))
        if (!schema.introspection.isEnabled) {
            builder.configureRuntimeWiring { wiring: RuntimeWiring.Builder ->
                this.enableIntrospection(
                    wiring
                )
            }
        }

        builder.configureRuntimeWiring { wiringBuilder ->
            wiringBuilder
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Time)
                .scalar(ExtendedScalars.Locale)
                .scalar(ExtendedScalars.LocalTime)
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.UUID)
                .scalar(ExtendedScalars.Url)
                .scalar(ExtendedScalars.Object)
        }
        builder.configureRuntimeWiring(annotatedControllerConfigurer)
//        wiringConfigurers.orderedStream().forEach { configurer: RuntimeWiringConfigurer? ->
//            builder.configureRuntimeWiring(
//                configurer!!
//            )
//        }
        sourceCustomizers.orderedStream().forEach { customizer: GraphQlSourceBuilderCustomizer ->
            customizer.customize(
                builder
            )
        }

        return builder.build()
    }

    @Bean
    @ConditionalOnMissingBean(name = ["notyBatchLoaderRegistry"])
    open fun notyBatchLoaderRegistry(): BatchLoaderRegistry {
        return DefaultBatchLoaderRegistry()
    }

    @Bean
    @ConditionalOnMissingBean(name = ["notyExecutionGraphQlService"])
    open fun notyExecutionGraphQlService(
        notyGraphQlSource: GraphQlSource,
        notyBatchLoaderRegistry: BatchLoaderRegistry,
    ): ExecutionGraphQlService {
        val service = DefaultExecutionGraphQlService(notyGraphQlSource)
        service.addDataLoaderRegistrar(notyBatchLoaderRegistry)
        return service
    }

    private fun enableIntrospection(wiring: RuntimeWiring.Builder): RuntimeWiring.Builder {
        return wiring.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY)
    }

    private fun resolveSchemaResources(
        resolver: ResourcePatternResolver, locations: Array<String>,
        extensions: Array<String>,
    ): Array<Resource> {
        val resources: MutableList<Resource> = ArrayList()
        for (location in locations) {
            for (extension in extensions) {
                resources.addAll(resolveSchemaResources(resolver, "$location*$extension"))
            }
        }
        return resources.toTypedArray()
    }

    private fun resolveSchemaResources(resolver: ResourcePatternResolver, pattern: String): List<Resource> {
        return listOf(*resolver.getResources(pattern))
    }

    private fun <T> toList(provider: ObjectProvider<T>): List<T> {
        return provider.orderedStream().collect(Collectors.toList())
    }
}