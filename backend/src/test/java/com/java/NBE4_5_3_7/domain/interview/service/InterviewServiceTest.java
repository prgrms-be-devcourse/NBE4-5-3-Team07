package com.java.NBE4_5_3_7.domain.interview.service;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto;
import com.java.NBE4_5_3_7.domain.interview.repository.BookmarkRepository;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository;
import com.java.NBE4_5_3_7.domain.member.entity.Member;
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository;
import com.java.NBE4_5_3_7.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    InterviewContentRepository interviewRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BookmarkRepository bookmarkRepository;

    @Mock
    InterviewContentLikeRepository likeRepository;

    @InjectMocks
    InterviewService service;

    @Test
    @DisplayName("특정 카테고리 헤드 질문 ID 리스트 조회 성공")
    void categoryHeadQuestion_success() {
        // given
        InterviewCategory category = InterviewCategory.SPRING;
        List<Long> expectedIds = List.of(1L, 3L, 5L);

        when(interviewRepository.findInterviewContentIdsByCategoryAndHeadTrueAndHeadIdIsNull(category))
                .thenReturn(expectedIds);

        // when
        List<Long> result = service.categoryHeadQuestion(category);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedIds);
    }

    @Test
    @DisplayName("특정 카테고리 헤드 질문 ID 리스트 조회 - 빈 리스트")
    void categoryHeadQuestion_emptyList() {
        // given
        InterviewCategory category = InterviewCategory.DATABASE;

        when(interviewRepository.findInterviewContentIdsByCategoryAndHeadTrueAndHeadIdIsNull(category))
                .thenReturn(List.of());

        // when
        List<Long> result = service.categoryHeadQuestion(category);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("키워드 목록 조회 성공")
    void showKeywordList_success() {
        // given
        List<String> expectedKeywords = List.of("DI", "JPA", "MVC", "REST");

        when(interviewRepository.findDistinctCategories())
                .thenReturn(expectedKeywords);

        // when
        List<String> result = service.showKeywordList();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyElementsOf(expectedKeywords);
    }

    @Test
    @DisplayName("키워드에 따른 헤드 질문 ID 리스트 반환 성공")
    void keywordHeadQuestion_success() {
        // given
        List<String> keywords = List.of("DI", "JPA");
        KeywordContentRequestDto requestDto = new KeywordContentRequestDto();
        requestDto.setKeywordList(keywords);

        List<Long> expectedIds = List.of(1L, 5L, 9L);

        when(interviewRepository.findInterviewKeyword(keywords))
                .thenReturn(expectedIds);

        // when
        List<Long> result = service.keywordHeadQuestion(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedIds);
    }

    @Test
    @DisplayName("북마크 추가 성공")
    void bookmark_add_success() {
        // given
        Long memberId = 10L;
        Long contentId = 1L;

        Member member = mock(Member.class);
        when(member.getBookmarks()).thenReturn(new ArrayList<>());

        InterviewContent content = mock(InterviewContent.class);
        when(content.getBookmarks()).thenReturn(new ArrayList<>());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(interviewRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(bookmarkRepository.existsByMemberAndInterviewContent(member, content)).thenReturn(false);

        // when
        String result = service.bookmark(memberId, contentId);

        // then
        assertThat(result).isEqualTo("내 노트에 등록하였습니다.");
        verify(bookmarkRepository).save(any(InterviewContentBookmark.class));
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void bookmark_remove_success() {
        // given
        Long memberId = 10L;
        Long contentId = 1L;

        Member member = mock(Member.class);
        List<InterviewContentBookmark> bookmarks = new ArrayList<>();
        when(member.getBookmarks()).thenReturn(bookmarks);

        InterviewContent content = mock(InterviewContent.class);
        when(content.getBookmarks()).thenReturn(new ArrayList<>());

        InterviewContentBookmark bookmark = mock(InterviewContentBookmark.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(interviewRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(bookmarkRepository.existsByMemberAndInterviewContent(member, content)).thenReturn(true);
        when(bookmarkRepository.findByMemberAndInterviewContent(member, content)).thenReturn(bookmark);

        // when
        String result = service.bookmark(memberId, contentId);

        // then
        assertThat(result).isEqualTo("내 노트에서 삭제하였습니다.");
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("내 북마크 조회 성공")
    void showMyBookmark_success() {
        // given
        Long memberId = 10L;

        Member member = mock(Member.class);

        InterviewContent content1 = new InterviewContent();
        content1.setQuestion("What is DI?");
        content1.setModelAnswer("DI는 의존성 주입입니다.");

        InterviewContent content2 = new InterviewContent();
        content2.setQuestion("What is JPA?");
        content2.setModelAnswer("JPA는 자바 ORM 기술입니다.");

        InterviewContentBookmark bookmark1 = mock(InterviewContentBookmark.class);
        when(bookmark1.getId()).thenReturn(1L);
        when(bookmark1.getInterviewContent()).thenReturn(content1);

        InterviewContentBookmark bookmark2 = mock(InterviewContentBookmark.class);
        when(bookmark2.getId()).thenReturn(2L);
        when(bookmark2.getInterviewContent()).thenReturn(content2);

        List<InterviewContentBookmark> bookmarks = List.of(bookmark1, bookmark2);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(member.getBookmarks()).thenReturn(bookmarks);

        // when
        List<BookmarkResponseDto> result = service.showMyBookmark(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContentId()).isEqualTo(1L);
        assertThat(result.get(0).getQuestion()).isEqualTo("What is DI?");
        assertThat(result.get(0).getAnswer()).isEqualTo("DI는 의존성 주입입니다.");
        assertThat(result.get(1).getContentId()).isEqualTo(2L);
        assertThat(result.get(1).getQuestion()).isEqualTo("What is JPA?");
        assertThat(result.get(1).getAnswer()).isEqualTo("JPA는 자바 ORM 기술입니다.");
    }

    @Test
    @DisplayName("내 북마크 조회 - 북마크 없음")
    void showMyBookmark_emptyList() {
        // given
        Long memberId = 10L;

        Member member = mock(Member.class);
        when(member.getBookmarks()).thenReturn(new ArrayList<>());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        List<BookmarkResponseDto> result = service.showMyBookmark(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void deleteNote_success() {
        // given
        Long noteId = 1L;
        Member member = mock(Member.class);

        InterviewContentBookmark bookmark = mock(InterviewContentBookmark.class);
        when(bookmark.getMember()).thenReturn(member);

        when(bookmarkRepository.findById(noteId)).thenReturn(Optional.of(bookmark));

        // when
        service.deleteNote(noteId, member);

        // then
        verify(bookmarkRepository).deleteById(noteId);
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 북마크 없음")
    void deleteNote_notFound() {
        // given
        Long noteId = 999L;
        Member member = mock(Member.class);

        when(bookmarkRepository.findById(noteId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.deleteNote(noteId, member))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("북마크를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 권한 없음")
    void deleteNote_unauthorized() {
        // given
        Long noteId = 1L;
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class); // 다른 사용자

        InterviewContentBookmark bookmark = mock(InterviewContentBookmark.class);
        when(bookmark.getMember()).thenReturn(member2);

        when(bookmarkRepository.findById(noteId)).thenReturn(Optional.of(bookmark));

        // when & then
        assertThatThrownBy(() -> service.deleteNote(noteId, member1))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("본인이 추가한 북마크만 삭제할 수 있습니다.");
    }

    // 랜덤 인터뷰 질문 조회 테스트 코드는 만들지 못함
}