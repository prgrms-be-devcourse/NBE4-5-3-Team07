package com.java.NBE4_5_3_7.domain.member.dto;

import com.java.NBE4_5_3_7.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.format.DateTimeFormatter;

@Getter
public class MemberDto {
    @NonNull
    private long id;
    @NonNull
    private String nickname;
    @NonNull
    private String profileImgUrl;
    @NonNull
    private String subscriptPlan;
    @NonNull
    private String subscribeEndDate;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileImgUrl = member.getProfileImgUrlOrDefaultUrl();
        this.subscriptPlan = member.getSubscriptionPlan().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.subscribeEndDate = member.getSubscribeEndDate().format(formatter);
    }
}
