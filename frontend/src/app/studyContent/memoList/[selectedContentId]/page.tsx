"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import CodeParticles from "@/app/components/common/CodeParticles";

type StudyMemoResponseDto = {
  memoId: number;
  memoContent: string;
  likeCount: number;
  likedByUser?: boolean;
  createdAt?: string;
};

const MemoList = () => {
  const router = useRouter();
  const { selectedContentId } = useParams();
  const [memoList, setMemoList] = useState<StudyMemoResponseDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [activeFilter, setActiveFilter] = useState<"recent" | "popular">(
    "recent"
  );
  const [searchTerm, setSearchTerm] = useState<string>("");

  // 이전 페이지 경로 저장
  const [previousPage, setPreviousPage] = useState<string | null>(null);

  useEffect(() => {
    // 이전 페이지 정보 저장
    const referrer = document.referrer;
    if (referrer && !previousPage) {
      setPreviousPage(referrer);
      sessionStorage.setItem("previousContentPage", referrer);
    } else {
      const storedPrevious = sessionStorage.getItem("previousContentPage");
      if (storedPrevious) setPreviousPage(storedPrevious);
    }

    const fetchMemoList = async () => {
      try {
        setLoading(true);
        const response = await fetch(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/studyMemo/list/${selectedContentId}`,
          {
            method: "GET",
            credentials: "include",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        if (!response.ok)
          throw new Error("메모 리스트를 불러오는 데 실패했습니다.");
        const data = await response.json();

        const enriched = await Promise.all(
          data.map(async (memo: any) => {
            const res = await fetch(
              `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/studyMemo/like/${memo.memoId}/status`,
              {
                method: "GET",
                credentials: "include",
              }
            );
            const status = await res.json();
            return {
              ...memo,
              likeCount: status.count,
              likedByUser: status.liked,
            };
          })
        );

        setMemoList(enriched);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchMemoList();
  }, [selectedContentId, previousPage]);

  const handleMemoLike = async (memoId: number) => {
    try {
      const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/studyMemo/like/${memoId}`,
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error("좋아요를 추가하는 데 실패했습니다.");
      }

      const responseMessage = await response.text();

      // 애니메이션 효과 적용
      const memoElement = document.getElementById(`memo-${memoId}`);
      if (memoElement) {
        memoElement.classList.add("pulse-animation");
        setTimeout(() => {
          memoElement.classList.remove("pulse-animation");
        }, 800);
      }

      if (responseMessage === "좋아요 추가") {
        setMemoList((prev) =>
          prev.map((memo) =>
            memo.memoId === memoId
              ? { ...memo, likeCount: memo.likeCount + 1, likedByUser: true }
              : memo
          )
        );
      } else if (responseMessage === "좋아요 취소") {
        setMemoList((prev) =>
          prev.map((memo) =>
            memo.memoId === memoId
              ? {
                  ...memo,
                  likeCount: Math.max(memo.likeCount - 1, 0),
                  likedByUser: false,
                }
              : memo
          )
        );
      }
    } catch (err: any) {
      console.error("좋아요 실패:", err.message);
    }
  };

  // 정렬 및 필터링된 메모 목록
  const filteredMemos = memoList
    .filter((memo) =>
      searchTerm
        ? memo.memoContent.toLowerCase().includes(searchTerm.toLowerCase())
        : true
    )
    .sort((a, b) => {
      if (activeFilter === "popular") {
        return b.likeCount - a.likeCount;
      } else {
        // 최신순 정렬 (createdAt이 없는 경우도 처리)
        if (!a.createdAt) return 1;
        if (!b.createdAt) return -1;
        return (
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
      }
    });

  // 이전 페이지로 이동
  const handleGoBack = () => {
    router.push("/studyContent"); // ✨ 무조건 studyContent로 이동
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString?: string) => {
    if (!dateString) return "";

    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    // 24시간 이내인 경우
    if (diffDays === 0) {
      const hours = date.getHours();
      const minutes = date.getMinutes();
      return `오늘 ${hours.toString().padStart(2, "0")}:${minutes
        .toString()
        .padStart(2, "0")}`;
    }

    // 7일 이내인 경우
    if (diffDays < 7) {
      return `${diffDays}일 전`;
    }

    // 그 외의 경우
    return `${date.getFullYear()}.${(date.getMonth() + 1)
      .toString()
      .padStart(2, "0")}.${date.getDate().toString().padStart(2, "0")}`;
  };

  return (
    <div className="min-h-screen w-full relative overflow-x-hidden">
      {/* 배경 그라데이션 */}
      <div className="fixed inset-0 z-0 bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-indigo-950">
        {/* 블러 배경 요소 */}
        <div className="absolute top-10 right-20 w-64 h-64 rounded-full bg-blue-300 dark:bg-blue-600 blur-3xl opacity-20"></div>
        <div className="absolute bottom-10 left-10 w-80 h-80 rounded-full bg-indigo-300 dark:bg-indigo-700 blur-3xl opacity-20"></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-72 h-72 rounded-full bg-purple-300 dark:bg-purple-700 blur-3xl opacity-20"></div>
      </div>

      <CodeParticles />

      <div className="max-w-4xl mx-auto px-4 py-8 sm:py-12 relative z-10">
        {/* 헤더 영역 */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400 text-transparent bg-clip-text mb-4 text-center">
            공개 메모 리스트
          </h1>
          <p className="text-gray-600 dark:text-gray-400 text-center max-w-2xl mx-auto">
            다른 사용자들이 작성한 메모를 확인하고 마음에 드는 내용에 좋아요를
            눌러보세요!
          </p>
        </div>

        {/* 검색 및 필터 영역 */}
        <div className="bg-white dark:bg-gray-800 bg-opacity-90 dark:bg-opacity-90 rounded-xl shadow-md p-4 mb-6 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="relative w-full sm:w-auto flex-1">
            <input
              type="text"
              placeholder="메모 내용 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-200 focus:ring-2 focus:ring-indigo-500 dark:focus:ring-indigo-400 focus:border-transparent transition-all"
            />
            <svg
              className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 dark:text-gray-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
          <div className="flex items-center gap-2 w-full sm:w-auto">
            <button
              onClick={() => setActiveFilter("recent")}
              className={`px-4 py-2 flex-1 sm:flex-none rounded-lg font-medium text-sm transition-all ${
                activeFilter === "recent"
                  ? "bg-indigo-600 dark:bg-indigo-500 text-white shadow-md"
                  : "bg-white dark:bg-gray-900 text-gray-700 dark:text-gray-300 border border-gray-200 dark:border-gray-700"
              }`}
            >
              최신순
            </button>
            <button
              onClick={() => setActiveFilter("popular")}
              className={`px-4 py-2 flex-1 sm:flex-none rounded-lg font-medium text-sm transition-all ${
                activeFilter === "popular"
                  ? "bg-indigo-600 dark:bg-indigo-500 text-white shadow-md"
                  : "bg-white dark:bg-gray-900 text-gray-700 dark:text-gray-300 border border-gray-200 dark:border-gray-700"
              }`}
            >
              인기순
            </button>
          </div>
        </div>

        {/* 로딩 상태 */}
        {loading ? (
          <div className="flex flex-col justify-center items-center h-64 bg-white dark:bg-gray-800 bg-opacity-90 dark:bg-opacity-90 rounded-xl shadow-lg p-6">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600 dark:border-indigo-400 mb-4"></div>
            <p className="text-gray-600 dark:text-gray-400 animate-pulse">
              메모를 불러오는 중...
            </p>
          </div>
        ) : error ? (
          <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 p-6 rounded-xl shadow-md">
            <div className="flex items-center">
              <svg
                className="w-6 h-6 mr-3 text-red-500 dark:text-red-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                ></path>
              </svg>
              <p className="font-medium">오류가 발생했습니다</p>
            </div>
            <p className="mt-2 ml-9">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="mt-4 ml-9 text-indigo-600 dark:text-indigo-400 font-medium hover:underline focus:outline-none"
            >
              새로고침
            </button>
          </div>
        ) : filteredMemos.length > 0 ? (
          <div className="space-y-4">
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-2 px-1">
              {filteredMemos.length}개의 메모
              {searchTerm && ` (검색어: "${searchTerm}")`}
            </p>
            {filteredMemos.map((memo) => (
              <div
                id={`memo-${memo.memoId}`}
                key={memo.memoId}
                className="bg-white dark:bg-gray-800 bg-opacity-95 dark:bg-opacity-95 rounded-xl shadow-md hover:shadow-lg p-6 transition-all duration-300 border border-gray-100 dark:border-gray-700"
              >
                <p className="text-gray-800 dark:text-gray-200 text-lg mb-5 whitespace-pre-wrap leading-relaxed">
                  {memo.memoContent}
                </p>
                <div className="flex flex-wrap items-center justify-between gap-4 border-t border-gray-100 dark:border-gray-700 pt-4 mt-2">
                  <div className="flex items-center gap-3">
                    <button
                      onClick={() => handleMemoLike(memo.memoId)}
                      className={`flex items-center justify-center w-10 h-10 rounded-full transition-colors ${
                        memo.likedByUser
                          ? "bg-red-50 dark:bg-red-900/20 text-red-500 dark:text-red-400"
                          : "bg-gray-50 dark:bg-gray-700 text-gray-400 dark:text-gray-500 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 hover:text-indigo-500 dark:hover:text-indigo-400"
                      }`}
                      aria-label="좋아요"
                    >
                      <svg
                        className="w-5 h-5"
                        fill={memo.likedByUser ? "currentColor" : "none"}
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                        />
                      </svg>
                    </button>
                    <span
                      className={`font-medium transition-all ${
                        memo.likedByUser
                          ? "text-red-500 dark:text-red-400"
                          : "text-gray-700 dark:text-gray-300"
                      }`}
                    >
                      {memo.likeCount}
                    </span>
                  </div>

                  {memo.createdAt && (
                    <div className="flex items-center text-sm text-gray-500 dark:text-gray-400">
                      <svg
                        className="w-4 h-4 mr-1 opacity-70"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                      {formatDate(memo.createdAt)}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center bg-white dark:bg-gray-800 bg-opacity-95 dark:bg-opacity-95 rounded-xl shadow-md p-10">
            <div className="bg-indigo-50 dark:bg-indigo-900/20 p-6 rounded-full mb-6">
              <svg
                className="w-16 h-16 text-indigo-500 dark:text-indigo-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="1.5"
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                ></path>
              </svg>
            </div>
            <h3 className="text-xl font-medium text-gray-700 dark:text-gray-300 mb-2">
              공개된 메모가 없습니다
            </h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 text-center max-w-sm">
              {searchTerm
                ? `"${searchTerm}" 검색어에 해당하는 메모가 없습니다.`
                : "이 컨텐츠에 대해 아직 공개된 메모가 없습니다. 첫 번째로 메모를 작성해보세요!"}
            </p>
          </div>
        )}

        {/* 하단 버튼 영역 */}
        <div className="mt-10 flex justify-center">
          <button
            onClick={handleGoBack}
            className="inline-flex items-center justify-center px-6 py-3 rounded-full bg-white dark:bg-gray-800 bg-opacity-95 dark:bg-opacity-95 hover:bg-opacity-100 dark:hover:bg-opacity-100 text-indigo-600 dark:text-indigo-400 font-medium shadow-md hover:shadow-lg transition-all border border-indigo-100 dark:border-indigo-800 group"
          >
            <svg
              className="w-5 h-5 mr-2 transform group-hover:-translate-x-1 transition-transform duration-200"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M10 19l-7-7m0 0l7-7m-7 7h18"
              />
            </svg>
            이전 페이지로 돌아가기
          </button>
        </div>
      </div>

      {/* 애니메이션 스타일 */}
      <style jsx>{`
        .pulse-animation {
          animation: pulse 0.8s cubic-bezier(0.4, 0, 0.6, 1);
        }

        @keyframes pulse {
          0%,
          100% {
            transform: scale(1);
          }
          50% {
            transform: scale(1.03);
          }
        }
      `}</style>
    </div>
  );
};

export default MemoList;
