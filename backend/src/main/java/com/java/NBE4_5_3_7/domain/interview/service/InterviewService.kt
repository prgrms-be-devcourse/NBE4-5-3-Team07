package com.java.NBE4_5_3_7.domain.interview.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.RandomRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.RandomResponseDto
import com.java.NBE4_5_3_7.domain.interview.repository.BookmarkRepository
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ThreadLocalRandom

@Service
@Transactional
class InterviewService(
    private val interviewRepository: InterviewContentRepository,
    private val memberRepository: MemberRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val likeRepository: InterviewContentLikeRepository
) {
    // 1. 면접 컨텐츠 ID -> 면접 컨텐츠 DTO
    fun showOneInterviewContent(id: Long, memberId: Long): InterviewResponseDto {
        val content = interviewRepository.findById(id)
            .orElseThrow { RuntimeException("해당 면접 컨텐츠를 찾을 수 없습니다.") }

        val member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }

        val likedByUser = likeRepository.findByInterviewContentAndMember(content, member).isPresent
        val likeCount = likeRepository.countByInterviewContent(content)

        val nextList =
            content.interviewContentId?.let { interviewRepository.findNextInterviewContent(it, Pageable.ofSize(1)) }
        val nextId = nextList?.firstOrNull()?.interviewContentId

        return InterviewResponseDto(
            content.interviewContentId,
            content.headId,
            content.tailId,
            content.question,
            content.modelAnswer,
            content.category.toString(),
            content.keyword,
            nextId,
            likeCount,
            likedByUser
        )
    }

    // 2. 머리질문 순서대로 머리질문의 id 값 list 를 반환.
    fun allHeadQuestion(): List<Long> {
        return interviewRepository.findInterviewContentIdsByHeadTrueAndHeadIdIsNull()
    }

    //3. 모든 질문에 대해 순서 랜덤하게
    fun showRandomInterviewContent(randomRequestDto: RandomRequestDto, memberId: Long): RandomResponseDto {
        val headList = randomRequestDto.indexList
        var randomValue: Long? = null

        var randomIndex = 0
        if (!headList.isEmpty()) {
            randomIndex = ThreadLocalRandom.current().nextInt(headList.size)
            randomValue = headList[randomIndex]
        }

        val randomId = headList[randomIndex]
        headList.remove(randomId)
        val interview = interviewRepository.findById(randomId).orElseThrow {
            RuntimeException(
                "해당 컨텐츠를 찾을 수 없습니다."
            )
        }
        val likeCount = likeRepository.countByInterviewContent(interview)
        val likedByUser =
            likeRepository.findByInterviewContentAndMember(interview, memberRepository.findById(memberId).orElseThrow {
                RuntimeException(
                    "해당 멤버를 찾을 수 없습니다."
                )
            }).isPresent

        return RandomResponseDto(
            headList,
            InterviewResponseDto(
                interview.interviewContentId,
                interview.headId,
                interview.tailId,
                interview.question,
                interview.modelAnswer,
                interview.category.toString(),
                interview.keyword,
                randomValue,
                likeCount,
                likedByUser
            )
        )
    }

    // 4-1. 특정 카테고리 머리질문 순서대로 머리질문의 id 값 list 를 반환.
    fun categoryHeadQuestion(category: InterviewCategory?): List<Long> {
        return interviewRepository.findInterviewContentIdsByCategoryAndHeadTrueAndHeadIdIsNull(category)
    }

    // 키워드 목록 조회
    fun showKeywordList(): List<String> {
        return interviewRepository.findDistinctCategories()
    }

    // 키워드 들을 받고, 해당 키워드 들의 헤더 질문 ID 리스트 반환
    fun keywordHeadQuestion(keywordContentRequestDto: KeywordContentRequestDto): List<Long> {
        return interviewRepository.findInterviewKeyword(keywordContentRequestDto.keywordList)
    }

    fun bookmark(memberId: Long, contentId: Long): String {
        val member = memberRepository.findById(memberId).orElseThrow {
            RuntimeException(
                "해당 멤버를 찾을 수 없습니다."
            )
        }
        val interviewContent = interviewRepository.findById(contentId).orElseThrow {
            RuntimeException(
                "해당 컨텐츠를 찾을 수 없습니다."
            )
        }
        if (bookmarkRepository.existsByMemberAndInterviewContent(member, interviewContent)) {
            val bookmark = bookmarkRepository.findByMemberAndInterviewContent(member, interviewContent)
            member.bookmarks.remove(bookmark)
            interviewContent.bookmarks.remove(bookmark)

            // 북마크 삭제
            bookmarkRepository.delete(bookmark)

            return "내 노트에서 삭제하였습니다."
        }

        val bookmark = InterviewContentBookmark.builder().member(member).interviewContent(interviewContent).build()

        bookmarkRepository.save(bookmark)

        member.bookmarks.add(bookmark)
        interviewContent.bookmarks.add(bookmark)

        return "내 노트에 등록하였습니다."
    }

    fun showMyBookmark(memberId: Long): List<BookmarkResponseDto> {
        val member = memberRepository.findById(memberId).orElseThrow {
            RuntimeException("해당 멤버를 찾을 수 없습니다.")
        }

        return member.bookmarks.map {
            BookmarkResponseDto(
                it.id ?: throw IllegalStateException("Bookmark ID is null"),
                it.interviewContent?.question ?: "질문 없음",
                it.interviewContent?.modelAnswer ?: "모범답안 없음"
            )
        }
    }

    fun deleteNote(noteId: Long, member: Member) {
        val bookmark = bookmarkRepository.findById(noteId).orElseThrow {
            ServiceException(
                "404",
                "북마크를 찾을 수 없습니다."
            )
        }

        if (bookmark.member != member) {
            throw ServiceException("403", "본인이 추가한 북마크만 삭제할 수 있습니다.")
        }

        bookmarkRepository.deleteById(noteId)
    }
}