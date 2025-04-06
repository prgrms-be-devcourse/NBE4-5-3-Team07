package com.java.NBE4_5_3_7.domain.study.dto.response

import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo

class StudyMemoResponseDto {
    var memoId: Long? = null
    var memoContent: String? = null
    var studyContentId: Long? = null
    var firstCategory: String? = null
    var title: String? = null
    var body: String? = null
    var likeCount: Int = 0

    constructor(studyMemo: StudyMemo, likeCount: Int) {
        this.memoId = studyMemo.id
        this.memoContent = studyMemo.memoContent
        this.studyContentId = studyMemo.studyContent?.study_content_id
        this.likeCount = likeCount
    }

    constructor(
        memoId: Long?,
        memoContent: String?,
        studyContentId: Long?,
        firstCategory: String?,
        title: String?,
        body: String?,
        likeCount: Int
    ) {
        this.memoId = memoId
        this.memoContent = memoContent
        this.studyContentId = studyContentId
        this.firstCategory = firstCategory
        this.title = title
        this.body = body
        this.likeCount = likeCount
    }

    constructor()
}
