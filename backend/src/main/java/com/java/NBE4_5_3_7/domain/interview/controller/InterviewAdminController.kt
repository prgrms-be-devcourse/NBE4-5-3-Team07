package com.java.NBE4_5_3_7.domain.interview.controller;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewContentAdminResponseDto;
import com.java.NBE4_5_3_7.domain.interview.service.InterviewAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "면접 질문 관리", description = "관리자용 API")
@RestController
@RequestMapping("/api/v1/admin/interview")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class InterviewAdminController {

    private final InterviewAdminService interviewAdminService;

    @Operation(summary = "카테고리별 키워드 조회", description = "각 카테고리 내 키워드 목록을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<String>>> getCategoryKeywords() {
        return ResponseEntity.ok(interviewAdminService.getCategoryKeywords());
    }

    @Operation(summary = "특정 카테고리의 모든 질문 조회", description = "선택한 카테고리에 속하는 모든 면접 질문 데이터를 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<InterviewContentAdminResponseDto>> getInterviewsByCategory(
            @Parameter(description = "조회할 카테고리", example = "DATABASE")
            @PathVariable("category") InterviewCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interviewAdminService.getInterviewsByCategory(category, page, size));
    }

    @Operation(summary = "특정 카테고리의 키워드에 해당하는 모든 질문 조회",
            description = "선택한 카테고리 내에서 특정 키워드를 포함하는 면접 질문을 조회합니다.")
    @GetMapping("/category/{category}/{keyword}")
    public ResponseEntity<Page<InterviewContentAdminResponseDto>> getInterviewsByCategoryAndKeyword(
            @Parameter(description = "조회할 카테고리", example = "DATABASE")
            @PathVariable("category") InterviewCategory category,
            @Parameter(description = "조회할 키워드", example = "sequence")
            @PathVariable("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interviewAdminService.getInterviewsByCategoryAndKeyword(category, keyword, page, size));
    }

    @Operation(summary = "특정 면접 질문 ID 조회", description = "면접 질문 ID를 이용하여 해당 데이터를 조회합니다.")
    @GetMapping("/{interviewContentId}")
    public ResponseEntity<InterviewContentAdminResponseDto> getInterviewContentById(
            @Parameter(description = "조회할 면접 질문 ID", example = "1")
            @PathVariable("interviewContentId") Long interviewContentId) {
        return ResponseEntity.ok(interviewAdminService.getInterviewContentById(interviewContentId));
    }

    @Operation(summary = "연관된 면접 질문 조회", description = "특정 면접 질문 ID를 기준으로 관련된 모든 꼬리 질문을 조회합니다.")
    @GetMapping("/{interviewContentId}/related")
    public ResponseEntity<List<InterviewContentAdminResponseDto>> getRelatedInterviewContents(
            @Parameter(description = "조회할 면접 질문 ID", example = "1")
            @PathVariable Long interviewContentId) {
        return ResponseEntity.ok(interviewAdminService.getInterviewContentWithAllTails(interviewContentId));
    }

    @Operation(summary = "특정 면접 질문 수정", description = "면접 질문 ID를 기준으로 카테고리, 키워드, 질문, 모범 답안을 수정합니다.")
    @PutMapping("/{interviewContentId}")
    public ResponseEntity<InterviewContentAdminResponseDto> updateInterviewContent(
            @PathVariable Long interviewContentId,
            @RequestBody InterviewContentAdminRequestDto requestDto) {
        return ResponseEntity.ok(interviewAdminService.updateInterviewContent(interviewContentId, requestDto));
    }

    @Operation(summary = "특정 면접 질문 삭제", description = "면접 질문 ID를 기준으로 해당 질문과 모든 꼬리 질문을 삭제합니다.")
    @DeleteMapping("/{interviewContentId}")
    public ResponseEntity<Void> deleteInterviewContent(
            @PathVariable Long interviewContentId) {
        interviewAdminService.deleteInterviewContentWithAllTails(interviewContentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "면접 질문 등록", description = """
        새로운 면접 질문을 등록합니다. 기존 질문의 꼬리 질문으로 추가할 수도 있습니다.
        
        - `headId`가 `null`이면 새로운 머리 질문이 됩니다.
        - `headId`가 존재하면, 해당 질문의 마지막 질문인지(`has_tail = 0`) 확인 후 꼬리 질문으로 추가합니다.
        """)
    @PostMapping
    public ResponseEntity<InterviewContentAdminResponseDto> createInterviewContent(
            @RequestBody InterviewContentAdminRequestDto requestDto) {
        return ResponseEntity.ok(interviewAdminService.createInterviewContent(requestDto));
    }
}