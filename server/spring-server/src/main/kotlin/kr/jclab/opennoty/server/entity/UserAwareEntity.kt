package kr.jclab.opennoty.server.entity

interface UserAwareEntity : TenantAwareEntity {
    val userId: String
        @get:JvmName("getUserId") get
}