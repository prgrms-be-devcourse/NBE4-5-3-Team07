package com.java.NBE4_5_3_7.domain.study.repository

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemoLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StudyMemoLikeRepository : JpaRepository<StudyMemoLike?, Int?> {
    fun countByStudyMemoId(studyMemoId: Long?): Int

    fun findByStudyMemoAndMember(studyMemo: StudyMemo?, member: Member): Optional<StudyMemoLike?>?
}
