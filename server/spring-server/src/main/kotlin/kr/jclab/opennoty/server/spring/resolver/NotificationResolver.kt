package kr.jclab.opennoty.server.spring.resolver

import graphql.GraphQLContext
import kr.jclab.opennoty.model.FilterGQL
import kr.jclab.opennoty.model.NotificationGQL
import kr.jclab.opennoty.model.NotificationsResultGQL
import kr.jclab.opennoty.model.NotyMethod
import kr.jclab.opennoty.server.authentication.UserAuthentication
import kr.jclab.opennoty.server.error.UnauthorizedException
import kr.jclab.opennoty.server.natsmodel.NotificationUpdatedPayload
import kr.jclab.opennoty.server.spring.NotyServer
import kr.jclab.opennoty.server.spring.service.NotyNotificationService
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class NotificationResolver(
    private val notyServer: NotyServer,
    private val notyNotificationService: NotyNotificationService,
) {
    companion object {
        private val log = LoggerFactory.getLogger(NotificationResolver::class.java)
    }

    @SubscriptionMapping("notificationSub")
    fun notificationSub(
        context: GraphQLContext,
    ): Flux<NotificationGQL> {
        return Flux.from { subscriber ->
            val userAuthentication = context.get<UserAuthentication?>(UserAuthentication.GRAPHQL_CONTEXT_KEY)
                ?: let {
                    subscriber.onError(UnauthorizedException())
                    return@from
                }

            try {
                val dispatcher = notyServer.natsConnection.createDispatcher()
                val natsSubscription = dispatcher.subscribe(
                    notyServer.getNatsUserIdSubjectName(
                        userAuthentication.tenantId,
                        userAuthentication.userId,
                        "noti"
                    )
                ) { message ->
                    val payload = notyServer.objectMapper.readValue(message.data, NotificationUpdatedPayload::class.java)
                    val notification = notyServer.entityRepository.getNotificationWithData(
                        tenantId = userAuthentication.tenantId,
                        notificationId = payload.notificationId,
                    )!!
                    subscriber.onNext(NotificationGQL.fromNotificationWithData(notification, payload.consumableData))
                }

                subscriber.onSubscribe(object : Subscription {
                    override fun request(n: Long) {
                        // request
                    }

                    override fun cancel() {
                        natsSubscription.unsubscribe()
                    }
                })
            } catch (e: Exception) {
                log.warn("error", e)
                subscriber.onError(e)
            }
        }
    }

    @QueryMapping("notificationsGet")
    fun notificationsGet(
        context: GraphQLContext,
        @Argument("filters") filters: List<FilterGQL>?,
        @Argument("pageSize") pageSize: Int,
        @Argument("pageNumber") pageNumber: Int,
    ): NotificationsResultGQL {
        val userAuthentication = context.get<UserAuthentication?>(UserAuthentication.GRAPHQL_CONTEXT_KEY)
            ?: throw UnauthorizedException()

        return notyNotificationService.getPagedNotifications(userAuthentication, listOf(NotyMethod.NOTIFICATION), filters, pageSize, pageNumber.coerceAtLeast(1))
    }

    @MutationMapping("notificationMarkRead")
    fun notificationMarkRead(
        context: GraphQLContext,
        @Argument("id") id: String,
        @Argument("markRead") markRead: Boolean?,
    ): Boolean {
        val userAuthentication = context.get<UserAuthentication?>(UserAuthentication.GRAPHQL_CONTEXT_KEY)
            ?: throw UnauthorizedException()

        val notification = notyNotificationService.markRead(userAuthentication, id, markRead ?: true)

        return notification != null
    }
}