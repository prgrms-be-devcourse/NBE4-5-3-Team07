package com.java.NBE4_5_3_7.domain.member.entity

enum class SubscriptionPlan(
    val planName: String,
    val price: Int,
    val durationDays: Int
) {
    FREE("FREE", 0, 0),
    PREMIUM("PREMIUM", 100, 30)
}
