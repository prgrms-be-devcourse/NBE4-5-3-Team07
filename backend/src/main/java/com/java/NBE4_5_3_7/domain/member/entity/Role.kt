package com.java.NBE4_5_3_7.domain.member.entity

// 한글 출력 및 DB저장을 위해 role을 지우지 말아주세요
enum class Role(val role: String) {
    ADMIN("관리자"),
    USER("사용자")
}