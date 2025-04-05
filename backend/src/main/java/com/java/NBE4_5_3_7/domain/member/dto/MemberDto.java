package com.java.NBE4_5_3_7.domain.member.dto;

import com.java.NBE4_5_3_7.domain.member.entity.Member;
import org.springframework.lang.NonNull;

import java.time.format.DateTimeFormatter;

public class MemberDto {
    @NonNull
    private final long id;
    @NonNull
    private final String nickname;
    @NonNull
    private final String profileImgUrl;
    @NonNull
    private final String subscriptPlan;
    @NonNull
    private final String subscribeEndDate;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileImgUrl = member.getProfileImgUrlOrDefaultUrl();
        this.subscriptPlan = member.getSubscriptionPlan().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.subscribeEndDate = member.getSubscribeEndDate().format(formatter);
    }

    @NonNull
    public long getId() {
        return this.id;
    }

    @NonNull
    public String getNickname() {
        return this.nickname;
    }

    @NonNull
    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }

    @NonNull
    public String getSubscriptPlan() {
        return this.subscriptPlan;
    }

    @NonNull
    public String getSubscribeEndDate() {
        return this.subscribeEndDate;
    }
}
