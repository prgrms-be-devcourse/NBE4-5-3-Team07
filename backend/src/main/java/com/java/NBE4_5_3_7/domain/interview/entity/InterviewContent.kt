package com.java.NBE4_5_3_7.domain.interview.entity

import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto
import jakarta.persistence.*

@Entity
open class InterviewContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var interviewContentId: Long? = null

    open var headId: Long? = null
    open var tailId: Long? = null

    @Enumerated(EnumType.STRING)
    open lateinit var category: InterviewCategory

    open var keyword: String? = null

    @Lob
    open lateinit var question: String

    @Lob
    @Column(name = "model_answer", columnDefinition = "TEXT")
    open lateinit var modelAnswer: String

    @Column(name = "is_head")
    open var isHead: Boolean = false

    @Column(name = "has_tail")
    open var hasTail: Boolean = false

    @OneToMany(mappedBy = "interviewContent", cascade = [CascadeType.ALL])
    open var bookmarks: MutableList<InterviewContentBookmark> = mutableListOf()

    companion object {

        @JvmStatic
        fun createNewHead(
            question: String,
            modelAnswer: String,
            category: InterviewCategory,
            keyword: String
        ): InterviewContent {
            return InterviewContent().apply {
                this.question = question
                this.modelAnswer = modelAnswer
                this.category = category
                this.keyword = keyword
                this.isHead = true
                this.hasTail = false
            }
        }

        fun createTail(
            headContent: InterviewContent,
            requestDto: InterviewContentAdminRequestDto
        ): InterviewContent {
            if (headContent.hasTail) {
                throw IllegalStateException("이미 꼬리 질문이 존재하는 질문입니다.")
            }

            return InterviewContent().apply {
                this.question = requestDto.question!!
                this.modelAnswer = requestDto.modelAnswer!!
                this.category = headContent.category
                this.keyword = requestDto.keyword
                this.headId = headContent.interviewContentId
                this.isHead = false
                this.hasTail = false
            }.also {
                headContent.tailId = it.interviewContentId
                headContent.hasTail = true
            }
        }
    }

    fun removeTail() {
        this.tailId = null
        this.hasTail = false
    }
}