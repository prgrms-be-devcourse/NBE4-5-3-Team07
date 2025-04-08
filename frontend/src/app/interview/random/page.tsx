"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import CodeParticles from "@/app/components/common/CodeParticles";
// 백엔드 DTO와 동일한 타입들
interface InterviewResponseDto {
  id: number;
  head_id: number | null;
  tail_id: number | null;
  question: string;
  model_answer: string;
  category: string;
  keyword: string;
  next_id: number | null;
  likeCount: number; // 좋아요 개수
  likedByUser: boolean; // 현재 사용자가 좋아요를 누른 여부
}

interface RandomRequestDto {
  indexList: number[];
}

interface RandomResponseDto {
  indexList: number[]; // 새롭게 업데이트된 ID 리스트 (이미 사용된 ID가 제거됨)
  interviewResponseDto: InterviewResponseDto; // 랜덤으로 선택된 질문
}

interface InterviewCommentResponseDto {
  commentId: number;
  comment: string;
  public: boolean;
  interviewContentId: number;
}

export default function RandomInterviewPage() {
  const router = useRouter();

  // 전체 머리 질문 ID 리스트
  const [headIds, setHeadIds] = useState<number[]>([]);
  // 랜덤 모드 시작 여부
  const [isRandomMode, setIsRandomMode] = useState<boolean>(false);

  // 현재 보여주는 질문
  const [currentInterview, setCurrentInterview] =
    useState<InterviewResponseDto | null>(null);

  // 다음 랜덤 요청 시 사용할 ID 리스트
  const [randomList, setRandomList] = useState<number[]>([]);

  // 로딩, 에러, 정답 보기 상태
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [showAnswer, setShowAnswer] = useState<boolean>(false);

  // "이전 질문 다시보기"를 위한 히스토리
  const [history, setHistory] = useState<InterviewResponseDto[]>([]);

  // 메모(댓글) 관련 상태
  const [commentText, setCommentText] = useState<string>("");
  const [isPublic, setIsPublic] = useState<boolean>(true);
  const [myMemos, setMyMemos] = useState<InterviewCommentResponseDto[]>([]);
  const [publicMemos, setPublicMemos] = useState<InterviewCommentResponseDto[]>(
    []
  );
  const [loadingMemos, setLoadingMemos] = useState<boolean>(false);
  const [memosError, setMemosError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"my" | "public" | null>(null);

  // 추가 상태: 북마크 응답 메시지
  const [bookmarkMessage, setBookmarkMessage] = useState<string>("");

  // (1) 컴포넌트 마운트 시 전체 머리 질문 ID 가져오기
  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/all`, {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) {
          if (res.status === 401) {
            router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
            return;
          }
          throw new Error("전체 질문 ID 리스트를 가져오는데 실패했습니다.");
        }
        return res.json();
      })
      .then((data: number[]) => {
        setHeadIds(Array.isArray(data) ? data : []);
      })
      .catch((err: Error) => {
        setError(err.message);
      });
  }, [router]);

  // (2) "실전 면접 대비" 시작
  const startRandomMode = () => {
    if (headIds.length === 0) {
      alert("머리 질문 ID 리스트가 없습니다.");
      return;
    }
    setIsRandomMode(true);
    // 랜덤 요청을 위한 리스트 초기화
    setRandomList(headIds);
    // 히스토리 초기화
    setHistory([]);
    // 첫 질문 랜덤 호출
    fetchRandomInterview(headIds);
  };

  // (3) 랜덤 질문 호출: POST /interview/random
  const fetchRandomInterview = async (indices: number[]) => {
    try {
      setLoading(true);
      setError(null);

      // 현재 질문이 있다면 history에 저장
      if (currentInterview) {
        setHistory((prev) => [...prev, currentInterview]);
      }

      const requestBody: RandomRequestDto = { indexList: indices };
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/random`,
        {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(requestBody),
        }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("랜덤 면접 질문을 가져오는데 실패했습니다.");
      }
      const data: RandomResponseDto = await res.json();

      // 새 질문과 업데이트된 indexList 반영
      setCurrentInterview(data.interviewResponseDto);
      setRandomList(data.indexList);
      setShowAnswer(false);
      setActiveTab(null);
      setLoading(false);
      setBookmarkMessage("");
    } catch (err: any) {
      setError(err.message);
      setLoading(false);
    }
  };

  // (4) "다음 랜덤 질문" 버튼
  const handleNextRandom = () => {
    if (randomList.length === 0) {
      alert("더 이상 질문이 없습니다!");
      return;
    }
    fetchRandomInterview(randomList);
  };

  // (5) "이전 질문 다시보기"
  const handlePreviousQuestion = () => {
    if (history.length === 0) return;
    const prev = history[history.length - 1];
    setHistory(history.slice(0, history.length - 1));
    setCurrentInterview(prev);
    setShowAnswer(false);
    setActiveTab(null);
  };

  // (6) 상위/꼬리 질문 보기 (재조회 방식 동일)
  const fetchInterviewById = async (id: number) => {
    try {
      setLoading(true);
      // 현재 질문이 있다면 히스토리에 저장
      if (currentInterview) {
        setHistory((prev) => [...prev, currentInterview]);
      }
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/${id}`,
        {
          credentials: "include",
        }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("면접 질문을 가져오는데 실패했습니다.");
      }
      const data: InterviewResponseDto = await res.json();
      setCurrentInterview(data);
      setLoading(false);
      setShowAnswer(false);
      setActiveTab(null);
      setBookmarkMessage("");
    } catch (err: any) {
      setError(err.message);
      setLoading(false);
    }
  };

  const handleHeadQuestion = () => {
    if (currentInterview?.head_id) {
      fetchInterviewById(currentInterview.head_id);
    }
  };

  const handleTailQuestion = () => {
    if (currentInterview?.tail_id) {
      fetchInterviewById(currentInterview.tail_id);
    }
  };

  // (7) 정답 보기 토글
  const toggleAnswer = () => {
    setShowAnswer((prev) => !prev);
  };

  // (8) 좋아요 토글 함수 – 좋아요 요청 후 최신 데이터 재조회
  const handleLikeToggle = async () => {
    if (!currentInterview) return;
    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/like?id=${currentInterview.id}`,
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("좋아요 요청에 실패했습니다.");
      }
      // 백엔드에서 최신 데이터를 재조회
      await fetchInterviewById(currentInterview.id);
    } catch (err: any) {
      alert(err.message);
    }
  };

  // (9) 메모(댓글) 저장 함수
  const handleCommentSubmit = async () => {
    if (!currentInterview) return;
    if (commentText.trim() === "") {
      alert("댓글을 입력하세요.");
      return;
    }
    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/interview/comment`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({
            comment: commentText,
            isPublic: isPublic,
            interviewContentId: currentInterview.id,
          }),
        }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("댓글 저장에 실패했습니다.");
      }
      setCommentText("");
      alert("댓글이 저장되었습니다.");
    } catch (err: any) {
      alert(err.message);
    }
  };

  // (10) 내 메모 보기 함수
  const fetchMyMemos = async () => {
    if (!currentInterview) return;
    setLoadingMemos(true);
    setMemosError(null);
    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/interview/comment/my/${currentInterview.id}`,
        { credentials: "include" }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("내 메모를 가져오는데 실패했습니다.");
      }
      const data: InterviewCommentResponseDto[] = await res.json();
      setMyMemos(data);
      setActiveTab("my");
    } catch (err: any) {
      setMemosError(err.message);
    } finally {
      setLoadingMemos(false);
    }
  };

  // (11) 다른 사람 메모 보기 함수
  const fetchPublicMemos = async () => {
    if (!currentInterview) return;
    setLoadingMemos(true);
    setMemosError(null);
    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/interview/comment/public/${currentInterview.id}`,
        { credentials: "include" }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("공개 메모를 가져오는데 실패했습니다.");
      }
      const data: InterviewCommentResponseDto[] = await res.json();
      setPublicMemos(data);
      setActiveTab("public");
    } catch (err: any) {
      setMemosError(err.message);
    } finally {
      setLoadingMemos(false);
    }
  };

  // (A) 북마크 토글 함수
  const handleBookmark = async () => {
    if (!currentInterview) return;
    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/bookmark?id=${currentInterview.id}`,
        {
          method: "POST",
          credentials: "include",
        }
      );
      if (!res.ok) {
        if (res.status === 401) {
          router.push(`${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`);
          return;
        }
        throw new Error("북마크 요청에 실패했습니다.");
      }
      const message = await res.text();
      setBookmarkMessage(message);
    } catch (err: any) {
      alert(err.message);
    }
  };

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-indigo-950 relative">
      {/* 배경 장식 요소 */}
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden opacity-20 pointer-events-none">
        <div className="absolute top-10 right-20 w-64 h-64 rounded-full bg-blue-300 dark:bg-blue-600 blur-3xl"></div>
        <div className="absolute bottom-10 left-10 w-80 h-80 rounded-full bg-indigo-300 dark:bg-indigo-700 blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-72 h-72 rounded-full bg-purple-300 dark:bg-purple-700 blur-3xl"></div>
      </div>

      <CodeParticles />

      {/* 메인 컨텐츠 */}
      <div className="container mx-auto max-w-4xl px-4 py-12 relative z-10">
        {error && (
          <div className="bg-red-100 dark:bg-red-900 border-l-4 border-red-500 text-red-700 dark:text-red-200 p-4 rounded mb-6">
            <p className="font-medium">오류 발생:</p>
            <p>{error}</p>
          </div>
        )}

        {!isRandomMode && (
          <div className="text-center mt-12">
            <h1 className="text-4xl font-bold mb-6 bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400 text-transparent bg-clip-text">
              기술 면접 준비
            </h1>
            <p className="text-gray-700 dark:text-gray-300 mb-8 max-w-3xl mx-auto">
              면접 질문을 학습하고 정답을 확인하세요. 나만의 메모를 작성하거나
              다른 개발자의 답변을 참고할 수 있습니다.
            </p>
            <button
              onClick={startRandomMode}
              disabled={headIds.length === 0}
              className="rounded-full bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white py-3 px-8 font-medium text-lg transition-all flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/20 mx-auto disabled:opacity-50 disabled:cursor-not-allowed"
            >
              질문 순서 랜덤하게 생성하기
            </button>
          </div>
        )}

        {isRandomMode && currentInterview && (
          <div className="mt-8">
            {loading && (
              <div className="flex justify-center items-center h-24">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 dark:border-indigo-400"></div>
              </div>
            )}

            {!loading && (
              <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 mb-6 transition-all">
                {/* 상단 헤더 영역: 카테고리/키워드, 북마크, 좋아요 버튼 */}
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6 pb-4 border-b border-gray-200 dark:border-gray-700">
                  <div className="flex flex-wrap gap-2 items-center">
                    <div className="px-3 py-1.5 bg-indigo-100 dark:bg-indigo-900 text-indigo-700 dark:text-indigo-300 rounded-md font-semibold">
                      {currentInterview.category.toUpperCase()} &gt;{" "}
                      {currentInterview.keyword}
                    </div>
                    <button
                      onClick={handleLikeToggle}
                      className={`inline-flex items-center px-3 py-1.5 rounded-md text-white transition-colors ${
                        currentInterview.likedByUser
                          ? "bg-red-500 hover:bg-red-600 dark:bg-red-600 dark:hover:bg-red-700"
                          : "bg-green-500 hover:bg-green-600 dark:bg-green-600 dark:hover:bg-green-700"
                      }`}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4 mr-1"
                        fill={
                          currentInterview.likedByUser ? "currentColor" : "none"
                        }
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                        />
                      </svg>
                      {currentInterview.likedByUser
                        ? `취소 (${currentInterview.likeCount})`
                        : `좋아요 (${currentInterview.likeCount})`}
                    </button>
                  </div>
                  <button
                    onClick={handleBookmark}
                    className="inline-flex items-center px-4 py-2 rounded-md bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-4 w-4 mr-1"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
                      />
                    </svg>
                    내 노트에 추가
                  </button>
                </div>

                {/* 북마크 응답 메시지 */}
                {bookmarkMessage && (
                  <div className="text-center text-indigo-600 dark:text-indigo-400 py-2">
                    {bookmarkMessage}
                  </div>
                )}

                {/* 질문 내용 */}
                <div className="mb-6">
                  <h2 className="text-lg font-medium mb-2 text-gray-600 dark:text-gray-400">
                    질문:
                  </h2>
                  <p className="text-xl text-gray-800 dark:text-gray-200 leading-relaxed">
                    {currentInterview.question}
                  </p>
                </div>

                {/* 정답 보기 토글 */}
                <div className="flex justify-center mb-6">
                  <button
                    onClick={toggleAnswer}
                    className={`inline-flex items-center px-4 py-2 rounded-md transition-colors ${
                      showAnswer
                        ? "bg-red-500 hover:bg-red-600 dark:bg-red-600 dark:hover:bg-red-700 text-white"
                        : "bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white"
                    }`}
                  >
                    {showAnswer ? (
                      <>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4 mr-1"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
                          />
                        </svg>
                        정답 가리기
                      </>
                    ) : (
                      <>
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4 mr-1"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                          />
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                          />
                        </svg>
                        정답 보기
                      </>
                    )}
                  </button>
                </div>

                {/* 정답 내용 */}
                {showAnswer && (
                  <div className="transition-all duration-300 ease-in-out overflow-hidden max-h-96 opacity-100 mb-6 bg-indigo-50 dark:bg-indigo-900/20 rounded-lg p-4">
                    <h2 className="text-lg font-medium mb-2 text-gray-600 dark:text-gray-400">
                      모범 답안:
                    </h2>
                    <p className="text-gray-800 dark:text-gray-200 leading-relaxed whitespace-pre-line">
                      {currentInterview.model_answer}
                    </p>
                  </div>
                )}

                {/* 탐색 버튼 그룹 */}
                <div className="flex flex-wrap gap-2 justify-center my-6">
                  {currentInterview.head_id && (
                    <button
                      onClick={handleHeadQuestion}
                      className="inline-flex items-center px-4 py-2 border border-indigo-600 dark:border-indigo-400 text-indigo-600 dark:text-indigo-400 rounded-md hover:bg-indigo-50 dark:hover:bg-indigo-900 transition-colors"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4 mr-1"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M7 11l5-5m0 0l5 5m-5-5v12"
                        />
                      </svg>
                      상위 질문 보기
                    </button>
                  )}
                  {currentInterview.tail_id && (
                    <button
                      onClick={handleTailQuestion}
                      className="inline-flex items-center px-4 py-2 border border-indigo-600 dark:border-indigo-400 text-indigo-600 dark:text-indigo-400 rounded-md hover:bg-indigo-50 dark:hover:bg-indigo-900 transition-colors"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4 mr-1"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z"
                        />
                      </svg>
                      꼬리 질문
                    </button>
                  )}
                  {history.length > 0 && (
                    <button
                      onClick={handlePreviousQuestion}
                      className="inline-flex items-center px-4 py-2 border border-indigo-600 dark:border-indigo-400 text-indigo-600 dark:text-indigo-400 rounded-md hover:bg-indigo-50 dark:hover:bg-indigo-900 transition-colors"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4 mr-1"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M11 19l-7-7 7-7m8 14l-7-7 7-7"
                        />
                      </svg>
                      이전 질문 다시보기
                    </button>
                  )}
                  <button
                    onClick={handleNextRandom}
                    className="inline-flex items-center px-4 py-2 border border-indigo-600 dark:border-indigo-400 text-indigo-600 dark:text-indigo-400 rounded-md hover:bg-indigo-50 dark:hover:bg-indigo-900 transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-4 w-4 mr-1"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M13 5l7 7-7 7M5 5l7 7-7 7"
                      />
                    </svg>
                    다음 질문
                  </button>
                </div>

                {/* 메모(댓글) 입력 영역 */}
                <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-6 mb-6">
                  <h3 className="text-lg font-bold text-center mb-4 bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400 text-transparent bg-clip-text">
                    나의 메모
                  </h3>
                  <textarea
                    value={commentText}
                    onChange={(e) => setCommentText(e.target.value)}
                    placeholder="질문에 대한 메모를 남겨보세요..."
                    className="w-full min-h-[120px] p-3 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 resize-y mb-4"
                  />
                  <div className="flex flex-wrap justify-between items-center">
                    <label className="flex items-center text-gray-700 dark:text-gray-300">
                      <input
                        type="checkbox"
                        checked={isPublic}
                        onChange={() => setIsPublic((prev) => !prev)}
                        className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500 mr-2"
                      />
                      다른 사용자에게 공개
                    </label>
                    <button
                      onClick={handleCommentSubmit}
                      className="inline-flex items-center px-4 py-2 rounded-md bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white transition-colors"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4 mr-1"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"
                        />
                      </svg>
                      저장하기
                    </button>
                  </div>
                </div>

                {/* 메모 조회 영역 */}
                <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden">
                  <div className="flex border-b border-gray-200 dark:border-gray-700">
                    <button
                      onClick={fetchMyMemos}
                      className={`flex-1 px-4 py-3 text-center font-medium ${
                        activeTab === "my"
                          ? "bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400"
                          : "text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                      }`}
                    >
                      내 메모 보기
                    </button>
                    <button
                      onClick={fetchPublicMemos}
                      className={`flex-1 px-4 py-3 text-center font-medium ${
                        activeTab === "public"
                          ? "bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400"
                          : "text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                      }`}
                    >
                      다른 사람 메모 보기
                    </button>
                  </div>
                  <div className="p-4">
                    {loadingMemos && (
                      <div className="flex justify-center items-center h-24">
                        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600 dark:border-indigo-400"></div>
                      </div>
                    )}

                    {memosError && (
                      <div className="text-red-500 dark:text-red-400 text-center py-4">
                        {memosError}
                      </div>
                    )}

                    {activeTab === "my" && !loadingMemos && (
                      <>
                        {myMemos.length > 0 ? (
                          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                            {myMemos.map((memo) => (
                              <li key={memo.commentId} className="py-3">
                                <p className="text-gray-800 dark:text-gray-200 whitespace-pre-line">
                                  {memo.comment}
                                </p>
                                <div className="mt-1 text-xs text-gray-500 dark:text-gray-400 flex items-center">
                                  <span
                                    className={`inline-block w-2 h-2 rounded-full mr-1 ${
                                      memo.public
                                        ? "bg-green-500"
                                        : "bg-yellow-500"
                                    }`}
                                  ></span>
                                  {memo.public ? "공개" : "비공개"}
                                </div>
                              </li>
                            ))}
                          </ul>
                        ) : (
                          <div className="text-center py-8 text-gray-600 dark:text-gray-400">
                            작성한 메모가 없습니다.
                          </div>
                        )}
                      </>
                    )}

                    {activeTab === "public" && !loadingMemos && (
                      <>
                        {publicMemos.length > 0 ? (
                          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                            {publicMemos.map((memo) => (
                              <li key={memo.commentId} className="py-3">
                                <p className="text-gray-800 dark:text-gray-200 whitespace-pre-line">
                                  {memo.comment}
                                </p>
                              </li>
                            ))}
                          </ul>
                        ) : (
                          <div className="text-center py-8 text-gray-600 dark:text-gray-400">
                            공개된 메모가 없습니다.
                          </div>
                        )}
                      </>
                    )}

                    {!activeTab && !loadingMemos && (
                      <div className="text-center py-8 text-gray-600 dark:text-gray-400">
                        탭을 선택하여 메모를 확인하세요.
                      </div>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
