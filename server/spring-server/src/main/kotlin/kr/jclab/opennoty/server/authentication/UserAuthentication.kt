package kr.jclab.opennoty.server.authentication

import kr.jclab.opennoty.server.entity.UserAwareEntity

interface UserAuthentication : UserAwareEntity {
    companion object {
        val GRAPHQL_CONTEXT_KEY = UserAuthentication::javaClass.name
    }
}
