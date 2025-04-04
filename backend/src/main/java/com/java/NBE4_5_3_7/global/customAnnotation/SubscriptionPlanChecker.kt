package com.java.NBE4_5_3_7.global.customAnnotation

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class SubscriptionPlanChecker(private val memberService: MemberService) {

    fun hasPremiumAccess(): Boolean {
        val member: Member = memberService.getMemberFromRq()

        require(member.getSubscriptionPlan() == SubscriptionPlan.PREMIUM) {
            throw AccessDeniedException("FREE 사용자는 접근할 수 없습니다.")
        }


        return true
    }
}
