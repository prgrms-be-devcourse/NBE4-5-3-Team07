package com.java.NBE4_5_3_7.domain.member.dto;

import com.java.NBE4_5_3_7.domain.member.entity.Member
import java.time.format.DateTimeFormatter

data class MemberDto(
    val id: Long,
    val nickname: String,
    val profileImgUrl: String,
    val subscriptPlan: String,
    val subscribeEndDate: String
) {
    constructor(member: Member) : this(
        id = member.id,
        nickname = member.nickname,
        profileImgUrl = member.getProfileImgUrlOrDefaultUrl(),
        subscriptPlan = member.subscriptionPlan.toString(),
        subscribeEndDate = member.subscribeEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    )
}
