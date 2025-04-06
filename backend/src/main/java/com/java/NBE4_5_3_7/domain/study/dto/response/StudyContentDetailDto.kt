package com.java.NBE4_5_3_7.domain.study.dto.response

import com.java.NBE4_5_3_7.domain.study.entity.StudyContent

class StudyContentDetailDto {
    var id: Long? = null
    var title: String? = null
    var body: String? = null
    var firstCategory: String? = null
    var secondCategory: String? = null

    constructor(studyContent: StudyContent) {
        this.id = studyContent.study_content_id
        this.title = studyContent.title
        this.body = studyContent.body
        this.firstCategory = studyContent.firstCategory.name
        this.secondCategory = studyContent.secondCategory
    }
}
