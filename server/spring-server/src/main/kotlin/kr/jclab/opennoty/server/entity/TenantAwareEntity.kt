package kr.jclab.opennoty.server.entity

interface TenantAwareEntity {
    val tenantId: String
}