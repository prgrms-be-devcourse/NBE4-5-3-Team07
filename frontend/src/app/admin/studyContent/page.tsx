"use client";

import { useState } from "react";
import Header from "./header";
import Sidebar from "./sidebar";
import Content from "./content";
import CodeParticles from "@/app/components/common/CodeParticles";

export default function AdminContentPage() {
  const [selectedFirstCategory, setSelectedFirstCategory] = useState<
    string | null
  >(null);
  const [selectedSecondCategory, setSelectedSecondCategory] = useState<
    string | null
  >(null);
  const [contents, setContents] = useState<any[]>([]);

  const handleCreateContent = (newContent: any) => {
    setContents((prev) => [newContent, ...prev]);
  };

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-indigo-950 overflow-hidden relative">
      {/* 배경 장식 요소 */}
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden opacity-20 pointer-events-none">
        <div className="absolute top-10 right-20 w-64 h-64 rounded-full bg-blue-300 dark:bg-blue-600 blur-3xl"></div>
        <div className="absolute bottom-10 left-10 w-80 h-80 rounded-full bg-indigo-300 dark:bg-indigo-700 blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-72 h-72 rounded-full bg-purple-300 dark:bg-purple-700 blur-3xl"></div>
      </div>

      <CodeParticles />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-4 py-4 relative z-10">
        <Header onCreate={handleCreateContent} />

        <div className="flex flex-col md:flex-row gap-8">
          <div className="md:w-1/4">
            <Sidebar
              selectedFirstCategory={selectedFirstCategory}
              setSelectedFirstCategory={setSelectedFirstCategory}
              selectedSecondCategory={selectedSecondCategory}
              setSelectedSecondCategory={setSelectedSecondCategory}
            />
          </div>

          <div className="md:w-3/4">
            <Content
              selectedFirstCategory={selectedFirstCategory}
              selectedSecondCategory={selectedSecondCategory}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
