"use client";

import { useEffect, useState } from "react";

const API_URL = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/admin/study`;

interface StudyContentCreateRequestDto {
  firstCategory: string;
  secondCategory: string;
  title: string;
  body: string;
}

interface ContentCreateModalProps {
  onClose: () => void;
  onCreate: (newContent: StudyContentCreateRequestDto) => void;
}

export default function ContentCreateModal({
  onClose,
  onCreate,
}: ContentCreateModalProps) {
  const [formData, setFormData] = useState<StudyContentCreateRequestDto>({
    firstCategory: "",
    secondCategory: "",
    title: "",
    body: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categories, setCategories] = useState<string[]>([]);

  useEffect(() => {
    fetch(`${API_URL}/all`, {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then(async (res) => {
        if (!res.ok) {
          const errorText = await res.text();
          throw new Error(`서버 오류 발생: ${res.status} - ${errorText}`);
        }
        return res.json();
      })
      .then((data) => {
        const firstKeys = Object.keys(data);
        setCategories(firstKeys);
        if (firstKeys.length > 0) {
          setFormData((prev) => ({ ...prev, firstCategory: firstKeys[0] }));
        }
      })
      .catch((err) => {
        console.error("카테고리를 불러오는 데 실패했습니다:", err);
        setError("카테고리를 불러오는 데 실패했습니다.");
      });
  }, []);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreate = async () => {
    if (
      !formData.firstCategory ||
      !formData.secondCategory ||
      !formData.title ||
      !formData.body
    ) {
      setError("모든 필드를 입력해주세요.");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`등록 실패: ${response.status} - ${errorText}`);
      }

      onCreate(formData);
      onClose();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-lg z-50 flex items-center justify-center">
      <div className="relative bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full mx-4 overflow-hidden">
        <div className="absolute top-0 right-0 -mr-4 -mt-4 w-32 h-32 rounded-full bg-indigo-300 dark:bg-indigo-800 opacity-20 blur-xl"></div>
        <div className="absolute bottom-0 left-0 -ml-4 -mb-4 w-32 h-32 rounded-full bg-purple-300 dark:bg-purple-800 opacity-20 blur-xl"></div>

        <div className="relative p-6">
          <h2 className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400 text-transparent bg-clip-text mb-6 text-center">
            학습 콘텐츠 등록
          </h2>

          {error && (
            <div className="mb-5 bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded">
              <p className="font-medium">오류</p>
              <p>{error}</p>
            </div>
          )}

          <div className="space-y-4">
            <div>
              <label className="block text-gray-700 dark:text-gray-300 font-medium mb-2">
                제목
              </label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
                placeholder="제목을 입력하세요"
              />
            </div>

            <div>
              <label className="block text-gray-700 dark:text-gray-300 font-medium mb-2">
                첫 번째 카테고리
              </label>
              <select
                name="firstCategory"
                value={formData.firstCategory}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white appearance-none bg-no-repeat bg-right"
                style={{
                  backgroundImage:
                    "url(\"data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e\")",
                  backgroundSize: "1.5em 1.5em",
                  paddingRight: "2.5rem",
                }}
              >
                {categories.length > 0 ? (
                  categories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))
                ) : (
                  <option disabled>카테고리를 불러오는 중...</option>
                )}
              </select>
            </div>

            <div>
              <label className="block text-gray-700 dark:text-gray-300 font-medium mb-2">
                두 번째 카테고리
              </label>
              <input
                type="text"
                name="secondCategory"
                value={formData.secondCategory}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white"
                placeholder="새로운 카테고리를 입력하세요"
              />
            </div>

            <div>
              <label className="block text-gray-700 dark:text-gray-300 font-medium mb-2">
                내용
              </label>
              <textarea
                name="body"
                value={formData.body}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white resize-none min-h-[150px]"
                placeholder="내용을 입력하세요"
              />
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={handleCreate}
                disabled={loading}
                className={`rounded-full bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white py-2 px-6 font-medium transition-all flex-1 shadow-lg shadow-indigo-500/20 ${
                  loading ? "opacity-70 cursor-not-allowed" : ""
                }`}
              >
                {loading ? "등록 중..." : "등록"}
              </button>
              <button
                onClick={onClose}
                className="rounded-full bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 text-gray-800 dark:text-gray-200 py-2 px-6 font-medium transition-all flex-1"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
