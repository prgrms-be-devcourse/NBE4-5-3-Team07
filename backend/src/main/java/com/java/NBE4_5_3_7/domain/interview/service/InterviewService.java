package com.java.NBE4_5_3_7.domain.interview.service;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.RandomRequestDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewResponseDto;
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.RandomResponseDto;
import com.java.NBE4_5_3_7.domain.interview.repository.BookmarkRepository;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository;
import com.java.NBE4_5_3_7.domain.member.entity.Member;
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository;
import com.java.NBE4_5_3_7.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class InterviewService {

    private final InterviewContentRepository interviewRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final InterviewContentLikeRepository likeRepository;

    public InterviewService(InterviewContentRepository interviewRepository, MemberRepository memberRepository, BookmarkRepository bookmarkRepository, InterviewContentLikeRepository likeRepository) {
        this.interviewRepository = interviewRepository;
        this.memberRepository = memberRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.likeRepository = likeRepository;
    }

    // 1. 면접 컨텐츠 ID -> 면접 컨텐츠 DTO
    public InterviewResponseDto showOneInterviewContent(Long id, Long memberId) {
        InterviewContent content = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 면접 컨텐츠를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));

        boolean likedByUser = likeRepository.findByInterviewContentAndMember(content, member).isPresent();
        Long likeCount = likeRepository.countByInterviewContent(content);

        Optional<InterviewContent> next = interviewRepository.findNextInterviewContent(content.getInterview_content_id());
        Long nextId = next.map(InterviewContent::getInterview_content_id).orElse(null);

        return new InterviewResponseDto(
                content.getInterview_content_id(),
                content.getHead_id(),
                content.getTail_id(),
                content.getQuestion(),
                content.getModelAnswer(),
                content.getCategory().toString(),
                content.getKeyword(),
                nextId,
                likeCount,
                likedByUser
        );
    }


    // 2. 머리질문 순서대로 머리질문의 id 값 list 를 반환.
    public List<Long> allHeadQuestion() {
        return interviewRepository.findInterviewContentIdsByHeadTrueAndHeadIdIsNull();
    }

    //3. 모든 질문에 대해 순서 랜덤하게
    public RandomResponseDto showRandomInterviewContent(RandomRequestDto randomRequestDto, Long memberId) {

        List<Long> headList = randomRequestDto.getIndexList();
        Long randomValue = null;

        int randomIndex = 0;
        if (!headList.isEmpty()) {
            randomIndex = ThreadLocalRandom.current().nextInt(headList.size());
            randomValue = headList.get(randomIndex);
        }

        Long randomId = headList.get(randomIndex);
        headList.remove(randomId);
        InterviewContent interview = interviewRepository.findById(randomId).orElseThrow(() -> new RuntimeException("해당 컨텐츠를 찾을 수 없습니다."));
        Long likeCount = likeRepository.countByInterviewContent(interview);
        boolean likedByUser = likeRepository.findByInterviewContentAndMember(interview, memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."))).isPresent();

        return new RandomResponseDto(headList, new InterviewResponseDto(interview.getInterview_content_id(), interview.getHead_id(), interview.getTail_id(), interview.getQuestion(), interview.getModelAnswer(), interview.getCategory().toString(), interview.getKeyword(), randomValue, likeCount, likedByUser));
    }

    // 4-1. 특정 카테고리 머리질문 순서대로 머리질문의 id 값 list 를 반환.
    public List<Long> categoryHeadQuestion(InterviewCategory category) {
        return interviewRepository.findInterviewContentIdsByCategoryAndHeadTrueAndHeadIdIsNull(category);
    }

    // 키워드 목록 조회
    public List<String> showKeywordList() {
        return interviewRepository.findDistinctCategories();
    }

    // 키워드 들을 받고, 해당 키워드 들의 헤더 질문 ID 리스트 반환
    public List<Long> keywordHeadQuestion(KeywordContentRequestDto keywordContentRequestDto) {
        return interviewRepository.findInterviewKeyword(keywordContentRequestDto.getKeywordList());
    }

    public String bookmark(Long memberId, Long contentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));
        InterviewContent interviewContent = interviewRepository.findById(contentId).orElseThrow(() -> new RuntimeException("해당 컨텐츠를 찾을 수 없습니다."));
        if (bookmarkRepository.existsByMemberAndInterviewContent(member, interviewContent)) {
            InterviewContentBookmark bookmark = bookmarkRepository.findByMemberAndInterviewContent(member, interviewContent);
            member.getBookmarks().remove(bookmark);
            interviewContent.getBookmarks().remove(bookmark);

            // 북마크 삭제
            bookmarkRepository.delete(bookmark);

            return "내 노트에서 삭제하였습니다.";
        }

        InterviewContentBookmark bookmark = InterviewContentBookmark.builder().member(member).interviewContent(interviewContent).build();

        bookmarkRepository.save(bookmark);

        member.getBookmarks().add(bookmark);
        interviewContent.getBookmarks().add(bookmark);

        return "내 노트에 등록하였습니다.";

    }

    public List<BookmarkResponseDto> showMyBookmark(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));
        List<InterviewContentBookmark> bookmarks = member.getBookmarks();
        List<BookmarkResponseDto> result = new ArrayList<>();

        for (InterviewContentBookmark bookmark : bookmarks) {
            result.add(new BookmarkResponseDto(bookmark.getId(), bookmark.getInterviewContent().getQuestion(), bookmark.getInterviewContent().getModelAnswer()));
        }

        return result;
    }

    public void deleteNote(Long noteId, Member member) {
        InterviewContentBookmark bookmark = bookmarkRepository.findById(noteId).orElseThrow(() -> new ServiceException("404", "북마크를 찾을 수 없습니다."));

        if (!bookmark.getMember().equals(member)) {
            throw new ServiceException("403", "본인이 추가한 북마크만 삭제할 수 있습니다.");
        }

        bookmarkRepository.deleteById(noteId);
    }
}
