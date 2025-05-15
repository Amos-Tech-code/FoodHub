package com.amos_tech_code.foodhub.data.model.response

data class NotificationListResponse(
    val notifications: List<Notification>,
    val unreadCount: Int
)

data class Notification(
    val createdAt: String,
    val id: String,
    val isRead: Boolean,
    val message: String,
    val orderId: String,
    val title: String,
    val type: String,
    val userId: String
)
