package kr.jclab.opennoty.server.spring.controller

import kr.jclab.opennoty.model.*
import kr.jclab.opennoty.server.spring.service.NotyPublishService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

// DO NOT ANNOTATION
open class NotyApiController(
    private val notyPublishService: NotyPublishService,
) {
    @RequestMapping(
        method = [RequestMethod.POST],
        path = ["/publish/{publishId}"],
    )
    fun newPublish(
        @RequestHeader(value = RequestConstants.HEADER_NAME_TENANT_ID, required = false) tenantId: String?,
        @PathVariable("publishId") publishId: String,
        @RequestBody requestBody: PublishRequestBody,
    ): ResponseEntity<BasicResponseBody> {
        notyPublishService.newPublish(tenantId ?: "", publishId, requestBody)
        return ResponseEntity.ok(BasicResponseBody(message = "success"))
    }

    @RequestMapping(
        method = [RequestMethod.PUT],
        path = ["/publish/{publishId}"],
    )
    fun updatePublish(
        @RequestHeader(value = RequestConstants.HEADER_NAME_TENANT_ID, required = false) tenantId: String?,
        @PathVariable("publishId") publishId: String,
        @RequestBody requestBody: PublishUpdateRequestBody,
    ): ResponseEntity<BasicResponseBody> {
        notyPublishService.updatePublish(tenantId ?: "", publishId, requestBody)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BasicResponseBody(message = "publishId not found"))
        return ResponseEntity.ok(BasicResponseBody(message = "success"))
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/publish/{publishId}"],
    )
    fun getPublish(
        @RequestHeader(value = RequestConstants.HEADER_NAME_TENANT_ID, required = false) tenantId: String?,
        @PathVariable("publishId") publishId: String,
    ): ResponseEntity<Any> {
        val publish = notyPublishService.getPublish(tenantId ?: "", publishId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BasicResponseBody(message = "publishId not found"))

        return ResponseEntity.ok(
            GetPublishResponseBody(
                id = publish.id,
                createdAt = publish.createdAt,
                updatedAt = publish.updatedAt,
                metadata = publish.metadata,
                data = publish.data,
                secureData = publish.secureData,
                recipients = publish.recipients,
            )
        )
    }
}