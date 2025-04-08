"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function EvaluationPage() {
  const router = useRouter();
  const [evaluation, setEvaluation] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      const result = localStorage.getItem("evaluationResult");
      console.log("evaluationResult:", result);
      if (result) {
        setEvaluation(result);
        localStorage.removeItem("evaluationResult");
      } else {
        router.push("/ai/techInterview");
      }
      setIsLoading(false);
    }, 300); // 300ms 정도 딜레이
    return () => clearTimeout(timer);
  }, [router]);

  // 평가 내용을 섹션별로 구분하는 함수
  const renderEvaluationContent = () => {
    if (!evaluation) return null;

    // 평가 내용이 있을 경우 그대로 출력
    return (
      <pre className="whitespace-pre-wrap text-gray-700 font-sans leading-relaxed">
        {evaluation}
      </pre>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white flex flex-col items-center py-10 px-4">
      {isLoading ? (
        <div className="flex flex-col items-center justify-center h-96">
          <div className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          <p className="mt-4 text-gray-600 font-medium">
            결과를 불러오는 중...
          </p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl p-8 transition-all duration-300 hover:shadow-2xl">
          <div className="flex items-center justify-center mb-8">
            <div className="bg-blue-500 h-10 w-1 rounded-full mr-4"></div>
            <h1 className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent">
              면접 평가 결과
            </h1>
            <div className="bg-blue-500 h-10 w-1 rounded-full ml-4"></div>
          </div>

          <div className="border border-gray-200 rounded-xl p-6 bg-gray-50 shadow-inner mb-6 overflow-hidden">
            <div className="h-[500px] overflow-y-auto pr-2 custom-scrollbar">
              {renderEvaluationContent()}
            </div>
          </div>

          <div className="flex justify-between mt-8">
            <button
              onClick={() => router.push("/")}
              className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-2.5 px-6 rounded-lg transition-colors duration-200"
            >
              홈으로
            </button>

            <button
              onClick={() => router.push("/ai/techInterview")}
              className="bg-purple-600 hover:bg-blue-600 text-white font-semibold py-2.5 px-8 rounded-lg shadow-md transition-all duration-200 transform hover:translate-y-px flex items-center"
            >
              <span>다시 면접하기</span>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 ml-2"
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
            </button>
          </div>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500 italic">
              이 평가 결과는 AI 기반으로 생성되었으며, 참고용으로만 활용해
              주세요.
            </p>
          </div>
        </div>
      )}

      <style jsx global>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 8px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: #c5d5e6;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: #a5b9d2;
        }
      `}</style>
    </div>
  );
}
