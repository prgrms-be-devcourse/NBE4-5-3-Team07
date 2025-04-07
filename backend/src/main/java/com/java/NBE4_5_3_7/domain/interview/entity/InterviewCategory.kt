package com.java.NBE4_5_3_7.domain.interview.entity

enum class InterviewCategory(val category: String) {
    DATABASE("데이터베이스"),
    NETWORK("네트워크"),
    OperatingSystem("운영체제"),
    SPRING("스프링");

    companion object {
        @JvmStatic
        fun fromString(category: String): InterviewCategory {
            return entries.find { it.category == category }
                ?: throw IllegalArgumentException("Unknown category: $category")
        }
    }
}