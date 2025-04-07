package com.java.NBE4_5_3_7.domain.study.dto.request

import io.swagger.v3.oas.annotations.media.Schema

class StudyContentCreateRequestDto {
    @JvmField
    @Schema(description = "첫 번째 카테고리 (기존에 존재하는 카테고리 중에서 선택)", example = "Database")
    var firstCategory: String? = null

    @Schema(description = "두 번째 카테고리", example = "SQL")
    var secondCategory: String? = null

    @Schema(description = "제목", example = "데이터베이스의 기본 개념")
    var title: String? = null

    @Schema(description = "내용", example = "데이터베이스는 데이터를 효율적으로 저장, 검색, 관리할 수 있도록 설계된 구조입니다.")
    var body: String? = null
}