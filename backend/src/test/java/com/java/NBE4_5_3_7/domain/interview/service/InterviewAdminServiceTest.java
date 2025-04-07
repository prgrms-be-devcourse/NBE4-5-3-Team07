package com.java.NBE4_5_3_7.domain.interview.service;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewContentAdminResponseDto;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentAdminRepository;
import com.java.NBE4_5_3_7.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewAdminServiceTest {

    @Mock
    InterviewContentAdminRepository repository;

    @InjectMocks
    InterviewAdminService service;


    @Test
    @DisplayName("카테고리 키워드 목록 조회 성공 (DATABASE)")
    void getCategoryKeywords_success() {
        // given
        InterviewCategory category = InterviewCategory.DATABASE;
        List<String> keywords = List.of(
                "sequence", "DBMS", "view", "정규화", "이상현상", "DB설계",
                "무결성", "Oracle", "Transaction", "JDBC", "Index", "MySQL"
        );

        when(repository.findUniqueCategories()).thenReturn(List.of(category));
        when(repository.findUniqueKeywordsByCategory(category)).thenReturn(keywords);

        // when
        Map<String, List<String>> result = service.getCategoryKeywords();

        // then
        assertThat(result).containsKey("DATABASE");
        assertThat(result.get("DATABASE")).containsExactlyElementsOf(keywords);
    }

    @Test
    @DisplayName("카테고리 목록이 없는 경우 - 예외 발생")
    void getCategoryKeywords_noCategories() {
        // given
        when(repository.findUniqueCategories()).thenReturn(List.of());

        // then
        assertThatThrownBy(() -> service.getCategoryKeywords())
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("등록된 면접 질문 카테고리");
    }

    @Test
    @DisplayName("특정 카테고리에 키워드가 없는 경우 - 빈 리스트 반환")
    void getCategoryKeywords_categoryWithoutKeywords() {
        // given
        InterviewCategory category = InterviewCategory.DATABASE;

        when(repository.findUniqueCategories()).thenReturn(List.of(category));
        when(repository.findUniqueKeywordsByCategory(category)).thenReturn(List.of());

        // when
        Map<String, List<String>> result = service.getCategoryKeywords();

        // then
        assertThat(result).containsKey("DATABASE");
        assertThat(result.get("DATABASE")).isEmpty();
    }

    @Test
    @DisplayName("카테고리별 면접 질문 조회 성공")
    void getInterviewsByCategory_success() {
        // Given
        InterviewCategory category = InterviewCategory.SPRING;
        int page = 0, size = 10;

        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(1L);
        content.setKeyword("DI");
        content.setCategory(category);
        content.setQuestion("DI란 무엇인가?");
        content.setModelAnswer("DI는 의존성을 외부에서 주입하는 방식입니다.");
        content.setHead(true);
        content.setHasTail(false);

        Page<InterviewContent> contentPage = new PageImpl<>(List.of(content));

        when(repository.findByCategory(eq(category), any(Pageable.class))).thenReturn(contentPage);
        when(repository.countLikesByInterviewContentId(anyLong())).thenReturn(5L);

        // When
        Page<InterviewContentAdminResponseDto> result = service.getInterviewsByCategory(category, page, size);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(content.getInterviewContentId());
        assertThat(result.getContent().getFirst().getLikeCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("카테고리별 면접 질문 조회 실패")
    void getInterviewsByCategory_noContent() {
        // Given
        InterviewCategory category = InterviewCategory.SPRING;
        Page<InterviewContent> emptyPage = new PageImpl<>(Collections.emptyList());
        when(repository.findByCategory(eq(category), any(Pageable.class))).thenReturn(emptyPage);

        // Expect
        assertThatThrownBy(() -> service.getInterviewsByCategory(category, 0, 10))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 카테고리에 속하는 면접 질문이 없습니다.");
    }

    @Test
    @DisplayName("카테고리와 키워드로 면접 질문 조회 성공")
    void getInterviewsByCategoryAndKeyword_success() {
        // Given
        InterviewCategory category = InterviewCategory.SPRING;
        String keyword = "DI";
        int page = 0, size = 10;

        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(1L);
        content.setKeyword(keyword);
        content.setCategory(category);
        content.setQuestion("DI란 무엇인가?");
        content.setModelAnswer("DI는 의존성을 외부에서 주입하는 방식입니다.");
        content.setHead(true);
        content.setHasTail(false);

        Page<InterviewContent> contentPage = new PageImpl<>(List.of(content));

        when(repository.findByCategoryAndKeyword(eq(category), eq(keyword), any(Pageable.class)))
                .thenReturn(contentPage);
        when(repository.countLikesByInterviewContentId(1L)).thenReturn(7L);

        // When
        Page<InterviewContentAdminResponseDto> result = service.getInterviewsByCategoryAndKeyword(category, keyword, page, size);

        // Then
        assertThat(result.getContent()).hasSize(1);
        InterviewContentAdminResponseDto dto = result.getContent().getFirst();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getKeyword()).isEqualTo("DI");
        assertThat(dto.getCategory()).isEqualTo(InterviewCategory.SPRING);
        assertThat(dto.getLikeCount()).isEqualTo(7L);
    }

    @Test
    @DisplayName("카테고리와 키워드로 면접 질문 조회 실패")
    void getInterviewsByCategoryAndKeyword_noContent() {
        // Given
        InterviewCategory category = InterviewCategory.SPRING;
        String keyword = "DI";

        when(repository.findByCategoryAndKeyword(eq(category), eq(keyword), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Expect
        assertThatThrownBy(() -> service.getInterviewsByCategoryAndKeyword(category, keyword, 0, 10))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 카테고리와 키워드를 포함하는 면접 질문이 없습니다.");
    }


    @Test
    @DisplayName("면접 질문 ID로 조회 성공")
    void getInterviewContentById_success() {
        // Given
        Long id = 1L;
        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(id);
        content.setKeyword("AOP");
        content.setCategory(InterviewCategory.SPRING);
        content.setQuestion("AOP란 무엇인가?");
        content.setModelAnswer("관점 지향 프로그래밍입니다.");
        content.setHead(true);
        content.setHasTail(false);

        when(repository.findById(id)).thenReturn(Optional.of(content));
        when(repository.countLikesByInterviewContentId(id)).thenReturn(10L);

        // When
        InterviewContentAdminResponseDto result = service.getInterviewContentById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getKeyword()).isEqualTo("AOP");
        assertThat(result.getLikeCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("면접 질문 ID로 조회 실패")
    void getInterviewContentById_notFound() {
        // Given
        long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Expect
        assertThatThrownBy(() -> service.getInterviewContentById(id))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 ID의 면접 질문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("면접 질문 및 모든 꼬리 질문 조회 성공")
    void getInterviewContentWithAllTails_success() {
        // Given
        InterviewCategory category = InterviewCategory.SPRING;

        InterviewContent main = new InterviewContent();
        main.setInterviewContentId(1L);
        main.setQuestion("메인 질문 1");
        main.setModelAnswer("메인 답변 1");
        main.setHasTail(true);
        main.setCategory(category);

        InterviewContent tail1 = new InterviewContent();
        tail1.setInterviewContentId(2L);
        tail1.setQuestion("꼬리 질문 1");
        tail1.setModelAnswer("꼬리 답변 1");
        tail1.setHasTail(true);
        tail1.setCategory(category);

        InterviewContent tail2 = new InterviewContent();
        tail2.setInterviewContentId(3L);
        tail2.setQuestion("꼬리 질문 2");
        tail2.setModelAnswer("꼬리 답변 2");
        tail2.setHasTail(false);
        tail2.setCategory(category);

        when(repository.findById(1L)).thenReturn(Optional.of(main));
        when(repository.countLikesByInterviewContentId(anyLong())).thenReturn(0L);
        when(repository.findRelatedQuestions(1L)).thenReturn(List.of(tail1));
        when(repository.findRelatedQuestions(2L)).thenReturn(List.of(tail2));

        // When
        List<InterviewContentAdminResponseDto> result = service.getInterviewContentWithAllTails(1L);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(2).getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("면접 질문 조회 실패")
    void getInterviewContentWithAllTails_notFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> service.getInterviewContentWithAllTails(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 ID의 면접 질문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("면접 질문 수정 성공")
    void updateInterviewContent_success() {
        // Given
        Long id = 1L;
        InterviewContent content = new InterviewContent();
        content.setInterviewContentId(id);
        content.setQuestion("old");
        when(repository.findById(id)).thenReturn(Optional.of(content));
        when(repository.countLikesByInterviewContentId(id)).thenReturn(3L);

        InterviewContentAdminRequestDto dto = new InterviewContentAdminRequestDto(
                InterviewCategory.SPRING, "new keyword", "new Q", "new A", null
        );

        // When
        InterviewContentAdminResponseDto result = service.updateInterviewContent(id, dto);

        // Then
        assertThat(result.getQuestion()).isEqualTo("new Q");
        assertThat(result.getModelAnswer()).isEqualTo("new A");
    }

    @Test
    @DisplayName("면접 질문 수정 실패")
    void updateInterviewContent_notFound() {
        // Given
        long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> service.updateInterviewContent(id, new InterviewContentAdminRequestDto()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 ID의 면접 질문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("면접 질문 및 꼬리 질문 재귀 삭제 성공")
    void deleteInterviewContentWithAllTails_success() {
        InterviewContent head = new InterviewContent();
        head.setInterviewContentId(1L);
        head.setHasTail(true);

        InterviewContent tail1 = new InterviewContent();
        tail1.setInterviewContentId(2L);
        tail1.setHasTail(false);
        tail1.setHeadId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(head));
        when(repository.findById(1L)).thenReturn(Optional.of(head));
        when(repository.findRelatedQuestions(1L)).thenReturn(List.of(tail1));

        service.deleteInterviewContentWithAllTails(1L);

        verify(repository).deleteAll(List.of(tail1));
        verify(repository).delete(head);
    }

    @Test
    @DisplayName("면접 질문 삭제 실패 - 존재하지 않는 ID")
    void deleteInterviewContentWithAllTails_notFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.deleteInterviewContentWithAllTails(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 ID의 면접 질문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("꼬리 질문 삭제 시 head 질문의 hasTail 갱신 확인")
    void deleteInterviewContentWithAllTails_withHeadUpdate() {
        // Given
        InterviewContent tail = new InterviewContent();
        tail.setInterviewContentId(2L);
        tail.setHasTail(false);
        tail.setHeadId(1L); // head가 존재하는 꼬리 질문

        InterviewContent head = new InterviewContent();
        head.setInterviewContentId(1L);
        head.setHasTail(true);

        when(repository.findById(2L)).thenReturn(Optional.of(tail));
        when(repository.findById(1L)).thenReturn(Optional.of(head));
        when(repository.findRelatedQuestions(2L)).thenReturn(List.of());

        // When
        service.deleteInterviewContentWithAllTails(2L);

        // Then
        verify(repository).save(head); // head의 hasTail → false 저장
        verify(repository).delete(tail);
    }

    @Test
    @DisplayName("면접 머리 질문 생성 성공")
    void createInterviewContent_head_success() {
        // Given
        InterviewContentAdminRequestDto dto = new InterviewContentAdminRequestDto(
                InterviewCategory.SPRING,
                "DI",
                "DI란 무엇인가?",
                "DI는 의존성을 외부에서 주입하는 방식입니다.",
                null // headId가 null이면 머리 질문
        );

        InterviewContent newContent = new InterviewContent();
        newContent.setInterviewContentId(1L);
        newContent.setCategory(dto.getCategory());
        newContent.setKeyword(dto.getKeyword());
        newContent.setQuestion(dto.getQuestion());
        newContent.setModelAnswer(dto.getModelAnswer());
        newContent.setHead(true);
        newContent.setHasTail(false);

        when(repository.save(any())).thenReturn(newContent);

        // When
        InterviewContentAdminResponseDto result = service.createInterviewContent(dto);

        // Then
        assertThat(result.getQuestion()).isEqualTo(dto.getQuestion());
        assertThat(result.getModelAnswer()).isEqualTo(dto.getModelAnswer());
        assertThat(result.getCategory()).isEqualTo(dto.getCategory());
        assertThat(result.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("꼬리 질문 생성 성공")
    void createInterviewContent_tail_success() {
        // Given
        InterviewContent head = InterviewContent.createNewHead("Q1", "A1", InterviewCategory.SPRING, "spring");
        head.setInterviewContentId(10L);

        InterviewContentAdminRequestDto request = new InterviewContentAdminRequestDto();
        request.setHeadId(10L);
        request.setQuestion("Q2");
        request.setModelAnswer("A2");
        request.setKeyword("spring");
        request.setCategory(InterviewCategory.SPRING);

        when(repository.findById(10L)).thenReturn(Optional.of(head));

        // 두 번 호출되는 save 모두 처리
        when(repository.save(any(InterviewContent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InterviewContentAdminResponseDto response = service.createInterviewContent(request);

        // Then
        assertThat(response.getQuestion()).isEqualTo("Q2");
        assertThat(response.getKeyword()).isEqualTo("spring");

        // save 두 번 호출되었는지 확인
        verify(repository, times(2)).save(any(InterviewContent.class));
    }

    @Test
    @DisplayName("중간 질문에 꼬리 질문 추가 실패 - 이미 꼬리 질문이 있는 경우")
    void createInterviewContent_tail_fail_midHasTail() {
        // Given
        InterviewContent head = new InterviewContent();
        head.setInterviewContentId(1L);
        head.setHasTail(true); // 이미 꼬리 질문이 있음 (중간 질문 상태)

        InterviewContentAdminRequestDto dto = new InterviewContentAdminRequestDto();
        dto.setHeadId(1L);
        dto.setCategory(InterviewCategory.SPRING);
        dto.setKeyword("spring");
        dto.setQuestion("중간 질문에 꼬리 추가");
        dto.setModelAnswer("실패해야 함");

        when(repository.findById(1L)).thenReturn(Optional.of(head));

        // When & Then
        assertThatThrownBy(() -> service.createInterviewContent(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("중간 질문");
    }

    @Test
    @DisplayName("꼬리 질문 생성 실패 - 존재하지 않는 head ID")
    void createInterviewContent_tail_fail_headNotFound() {
        // Given
        InterviewContentAdminRequestDto dto = new InterviewContentAdminRequestDto();
        dto.setHeadId(999L);
        dto.setCategory(InterviewCategory.SPRING);
        dto.setKeyword("spring");
        dto.setQuestion("Q?");
        dto.setModelAnswer("A");

        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.createInterviewContent(dto))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 ID의 면접 질문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("재귀적으로 꼬리 질문 삭제 확인")
    void deleteInterviewContentWithAllTails_recursive() {
        // Given
        InterviewContent head = new InterviewContent();
        head.setInterviewContentId(1L);
        head.setHasTail(true);

        InterviewContent tail1 = new InterviewContent();
        tail1.setInterviewContentId(2L);
        tail1.setHeadId(1L);
        tail1.setHasTail(true);

        InterviewContent tail2 = new InterviewContent();
        tail2.setInterviewContentId(3L);
        tail2.setHeadId(2L);
        tail2.setHasTail(false);

        when(repository.findById(1L)).thenReturn(Optional.of(head));
        when(repository.findRelatedQuestions(1L)).thenReturn(List.of(tail1));
        when(repository.findRelatedQuestions(2L)).thenReturn(List.of(tail2));

        // When
        service.deleteInterviewContentWithAllTails(1L);

        // Then
        ArgumentCaptor<List<InterviewContent>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).deleteAll(captor.capture());

        List<InterviewContent> deleted = captor.getValue();
        assertThat(deleted).containsExactlyInAnyOrder(tail1, tail2);
        verify(repository).delete(head);
    }
}