package com.java.NBE4_5_3_7.domain.member.entity;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark;
import com.java.NBE4_5_3_7.global.entity.BaseEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity {

    @Column(length = 100, unique = true)
    private String username;
    @Column(length = 100, unique = true)
    private String apiKey;
    @Column(length = 100)
    private String nickname;
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;


    private LocalDateTime subscribeEndDate;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewContentBookmark> bookmarks = new ArrayList<>();

    public Member(String username, String apiKey, String nickname, String profileImgUrl, Role role, SubscriptionPlan subscriptionPlan, LocalDateTime subscribeEndDate, List<InterviewContentBookmark> bookmarks) {
        this.username = username;
        this.apiKey = apiKey;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.role = role;
        this.subscriptionPlan = subscriptionPlan;
        this.subscribeEndDate = subscribeEndDate;
        this.bookmarks = bookmarks;
    }

    public Member() {
    }

    protected Member(MemberBuilder<?, ?> b) {
        super(b);
        this.username = b.username;
        this.apiKey = b.apiKey;
        this.nickname = b.nickname;
        this.profileImgUrl = b.profileImgUrl;
        this.role = b.role;
        this.subscriptionPlan = b.subscriptionPlan;
        this.subscribeEndDate = b.subscribeEndDate;
        this.bookmarks = b.bookmarks;
    }

    public static MemberBuilder<?, ?> builder() {
        return new MemberBuilderImpl();
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getMemberAuthoritesAsString()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

    }

    public List<String> getMemberAuthoritesAsString() {

        List<String> authorities = new ArrayList<>();

        authorities.add("ROLE_USER");

        if (isAdmin()) {
            authorities.add("ROLE_ADMIN");
        } else {
            authorities.add("ROLE_USER");
        }

        return authorities;
    }

    public void update(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImgUrlOrDefaultUrl() {
        return (profileImgUrl == null || profileImgUrl.isBlank()) ? "https://placehold.co/640x640?text=O_O" : this.profileImgUrl;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return this.subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDateTime getSubscribeEndDate() {
        return this.subscribeEndDate;
    }

    public void setSubscribeEndDate(LocalDateTime subscribeEndDate) {
        this.subscribeEndDate = subscribeEndDate;
    }

    public List<InterviewContentBookmark> getBookmarks() {
        return this.bookmarks;
    }

    public void setBookmarks(List<InterviewContentBookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public static abstract class MemberBuilder<C extends Member, B extends MemberBuilder<C, B>> extends BaseEntityBuilder<C, B> {
        private String username;
        private String apiKey;
        private String nickname;
        private String profileImgUrl;
        private Role role;
        private SubscriptionPlan subscriptionPlan;
        private LocalDateTime subscribeEndDate;
        private List<InterviewContentBookmark> bookmarks;

        public B username(String username) {
            this.username = username;
            return self();
        }

        public B apiKey(String apiKey) {
            this.apiKey = apiKey;
            return self();
        }

        public B nickname(String nickname) {
            this.nickname = nickname;
            return self();
        }

        public B profileImgUrl(String profileImgUrl) {
            this.profileImgUrl = profileImgUrl;
            return self();
        }

        public B role(Role role) {
            this.role = role;
            return self();
        }

        public B subscriptionPlan(SubscriptionPlan subscriptionPlan) {
            this.subscriptionPlan = subscriptionPlan;
            return self();
        }

        public B subscribeEndDate(LocalDateTime subscribeEndDate) {
            this.subscribeEndDate = subscribeEndDate;
            return self();
        }

        public B bookmarks(List<InterviewContentBookmark> bookmarks) {
            this.bookmarks = bookmarks;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "Member.MemberBuilder(super=" + super.toString() + ", username=" + this.username + ", apiKey=" + this.apiKey + ", nickname=" + this.nickname + ", profileImgUrl=" + this.profileImgUrl + ", role=" + this.role + ", subscriptionPlan=" + this.subscriptionPlan + ", subscribeEndDate=" + this.subscribeEndDate + ", bookmarks=" + this.bookmarks + ")";
        }
    }

    private static final class MemberBuilderImpl extends MemberBuilder<Member, MemberBuilderImpl> {
        private MemberBuilderImpl() {
        }

        protected MemberBuilderImpl self() {
            return this;
        }

        public Member build() {
            return new Member(this);
        }
    }
}
