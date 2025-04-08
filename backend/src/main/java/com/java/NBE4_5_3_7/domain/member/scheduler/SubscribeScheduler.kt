package com.java.NBE4_5_3_7.domain.member.scheduler

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional
class SubscribeScheduler(
    private val memberRepository: MemberRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 0 * * ?")
    fun updateMemberSubscriptionPlan() {
        val allMember: List<Member> = memberRepository.findAll()

        for (member in allMember) {
            if (member.subscribeEndDate.isAfter(LocalDateTime.now())) {
                member.subscriptionPlan = SubscriptionPlan.FREE
                log.info(
                    "member Id : {}, member Nickname : {} : PREMIUM 기간 만료로 인해 FREE 로 변경되었습니다.",
                    member.id, member.nickname
                )
            }
        }
    }
}
