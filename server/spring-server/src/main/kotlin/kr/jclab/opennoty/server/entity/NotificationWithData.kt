package kr.jclab.opennoty.server.entity

interface NotificationWithData : Notification {
    val publish: Publish
}