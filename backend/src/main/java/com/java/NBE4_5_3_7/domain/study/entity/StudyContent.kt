package com.java.NBE4_5_3_7.domain.study.entity

import jakarta.persistence.*

@Entity
class StudyContent() {
    @JvmField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var study_content_id: Long? = null

    @JvmField
    @Column(name = "first_category")
    @Enumerated(EnumType.STRING)
    var firstCategory: FirstCategory? = null

    @JvmField
    @Column(name = "second_category")
    var secondCategory: String? = null

    @JvmField
    var title: String? = null

    @JvmField
    @Lob
    @Column(name = "body", columnDefinition = "TEXT")
    var body: String? = null

    constructor(title: String, body: String, firstCategory: FirstCategory, secondCategory: String) : this() {
        this.title = title
        this.body = body
        this.firstCategory = firstCategory
        this.secondCategory = secondCategory
    }
}
