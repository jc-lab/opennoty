package kr.jclab.opennoty.demo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.connection.ClusterSettings
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionStatus
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Configuration
class MongoConfig {
    @Bean("mongoTransactionManager")
    fun mongoTransactionManager(
        properties: MongoProperties,
        dbFactory: MongoDatabaseFactory
    ): PlatformTransactionManager {
        var isReplicaSet = properties.replicaSetName?.isNotEmpty() ?: false
        if (!isReplicaSet && properties.uri != null) {
            val uri = URI.create(properties.uri)
            if (uri.query.contains(Regex("replicaSet", RegexOption.IGNORE_CASE))) {
                isReplicaSet = true
            }
        }
        if (isReplicaSet) {
            return MongoTransactionManager(dbFactory)
        } else {
            return DummyMongoTransactionManager()
        }
    }

    class DummyMongoTransactionManager : AbstractPlatformTransactionManager() {
        override fun doGetTransaction(): Any {
            return Object()
        }

        override fun doBegin(transaction: Any, definition: TransactionDefinition) {
        }

        override fun doCommit(status: DefaultTransactionStatus) {
        }

        override fun doRollback(status: DefaultTransactionStatus) {
        }
    }

    @Bean
    fun mongoPropertiesClientSettingsBuilderCustomizer(
        properties: MongoProperties,
        environment: Environment?,
    ): CredentialToUriMongoClientSettingsBuilderCustomizer {
        return CredentialToUriMongoClientSettingsBuilderCustomizer(
            properties,
            environment
        )
    }

    class CredentialToUriMongoClientSettingsBuilderCustomizer(
        val properties: MongoProperties,
        val environment: Environment?,
    ) :
        MongoClientSettingsBuilderCustomizer {
        override fun customize(settingsBuilder: MongoClientSettings.Builder) {
            applyUuidRepresentation(settingsBuilder)
            applyHostAndPort(settingsBuilder)
            applyCredentials(settingsBuilder)
            applyReplicaSet(settingsBuilder)
        }

        private fun applyUuidRepresentation(builder: MongoClientSettings.Builder) {
            builder.uuidRepresentation(this.properties.uuidRepresentation)
        }

        private fun applyHostAndPort(builder: MongoClientSettings.Builder) {
            if (getEmbeddedPort() != null) {
                builder.applyConnectionString(ConnectionString("mongodb://localhost:" + getEmbeddedPort()))
                return
            }
            if (this.properties.uri != null) {
                builder.applyConnectionString(ConnectionString(this.properties.uri))
                return
            }
            if (this.properties.host != null || this.properties.port != null) {
                val host = getOrDefault(this.properties.host, "localhost")
                val port = getOrDefault(this.properties.port, MongoProperties.DEFAULT_PORT)
                val serverAddress = ServerAddress(host, port)
                builder.applyToClusterSettings { cluster: ClusterSettings.Builder ->
                    cluster.hosts(
                        listOf(serverAddress)
                    )
                }
                return
            }
            builder.applyConnectionString(ConnectionString(MongoProperties.DEFAULT_URI))
        }

        private fun applyCredentials(builder: MongoClientSettings.Builder) {
            if ((this.properties.username != null) && (this.properties.password != null)) {
                val database = this.properties.uri
                    ?.let { UriComponentsBuilder.fromUriString(it).build().queryParams["authSource"]?.first() }
                    ?: this.properties.authenticationDatabase
                    ?: this.properties.mongoClientDatabase
                builder.credential(
                    MongoCredential.createCredential(
                        this.properties.username,
                        database,
                        this.properties.password
                    )
                )
            }
        }

        private fun applyReplicaSet(builder: MongoClientSettings.Builder) {
            if (this.properties.replicaSetName != null) {
                builder.applyToClusterSettings { cluster: ClusterSettings.Builder ->
                    cluster.requiredReplicaSetName(
                        this.properties.replicaSetName
                    )
                }
            }
        }

        private fun <V> getOrDefault(value: V?, defaultValue: V): V {
            return value ?: defaultValue
        }

        private fun getEmbeddedPort(): Int? {
            if (this.environment != null) {
                val localPort = this.environment.getProperty("local.mongo.port")
                if (localPort != null) {
                    return Integer.valueOf(localPort)
                }
            }
            return null
        }
    }
}