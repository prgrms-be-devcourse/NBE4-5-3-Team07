package com.java.NBE4_5_3_7.domain.study.entity

enum class FirstCategory(val category: String) {
    ComputerArchitecture("컴퓨터구조"),
    DataStructure("자료구조"),
    Database("데이터베이스"),
    Network("네트워크"),
    OperatingSystem("운영체제"),
    SoftwareEngineering("소프트웨어엔지니어링"),
    Web("웹");

    companion object {
        fun fromString(category: String): FirstCategory {
            for (c in entries) {
                if (c.category.equals(category, ignoreCase = true) || c.name.equals(category, ignoreCase = true)) {
                    return c
                }
            }
            throw IllegalArgumentException("Unknown category: $category")
        }
    }
}
