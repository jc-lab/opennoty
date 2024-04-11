package kr.jclab.opennoty.demo.config

import kr.jclab.opennoty.server.authentication.UserAuthentication
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthWebSocketGraphQlInterceptor : WebSocketGraphQlInterceptor {
    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        request.uri.queryParams.getFirst("userId")
            ?.also { userId ->
                request.configureExecutionInput { executionInput,  builder ->
                    val authentication = UserAuthenticationImpl(
                        tenantId = "testcompany",
                        userId = userId,
                    )
                    val graphqlContext = HashMap<Any, Any>()
                    graphqlContext[UserAuthentication.GRAPHQL_CONTEXT_KEY] = authentication
                    builder.graphQLContext(graphqlContext).build()
                }
            }
        return chain.next(request)
    }

    class UserAuthenticationImpl(
        override val tenantId: String,
        override val userId: String,
    ) : UserAuthentication
}
