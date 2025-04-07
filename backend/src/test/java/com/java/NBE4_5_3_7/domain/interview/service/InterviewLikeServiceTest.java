package com.java.NBE4_5_3_7.domain.interview.service;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentLike;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository;
import com.java.NBE4_5_3_7.domain.member.entity.Member;
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewLikeServiceTest {

    @InjectMocks
    InterviewLikeService service;

    @Mock
    InterviewContentRepository interviewContentRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    InterviewContentLikeRepository interviewContentLikeRepository;

    @Mock
    RedissonClient redissonClient;

    @Mock
    RLock rLock;

    @Test
    @DisplayName("좋아요 추가 성공")
    void interviewLike_add_success() throws Exception {
        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(1L);
        Member member = mock(Member.class);

        when(redissonClient.getLock(any())).thenReturn(rLock);
        when(rLock.tryLock(5L, 10L, TimeUnit.SECONDS)).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(interviewContentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(interviewContentLikeRepository.findByInterviewContentAndMember(content, member))
                .thenReturn(Optional.empty());

        String result = service.interviewLike(1L, 1L);

        assertThat(result).isEqualTo("좋아요 추가");
        verify(interviewContentLikeRepository).save(any());
        verify(rLock).unlock();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void interviewLike_cancel_success() throws Exception {
        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(1L);
        Member member = mock(Member.class);

        InterviewContentLike like = new InterviewContentLike(content, member);

        when(redissonClient.getLock(any())).thenReturn(rLock);
        when(rLock.tryLock(5L, 10L, TimeUnit.SECONDS)).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(interviewContentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(interviewContentLikeRepository.findByInterviewContentAndMember(content, member))
                .thenReturn(Optional.of(like));

        String result = service.interviewLike(1L, 1L);

        assertThat(result).isEqualTo("좋아요 취소");
        verify(interviewContentLikeRepository).delete(like);
        verify(rLock).unlock();
    }
}
