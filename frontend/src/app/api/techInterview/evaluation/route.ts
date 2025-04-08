import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  try {
    const cookie = req.headers.get("cookie");
    const body = await req.json();
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/interview/evaluation`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(cookie ? { cookie } : {}), // 쿠키 헤더 추가
        },
        credentials: "include",
        body: JSON.stringify(body),
      }
    );
    if (!response.ok) {
      if (response.status === 401) {
        return NextResponse.redirect(
          `${process.env.NEXT_PUBLIC_FRONTEND_URL}/login`
        );
      }
      if (response.status === 403) {
        // 403 응답일 경우, 에러 메시지를 그대로 클라이언트에 전달
        const errorText = await response.text();
        return NextResponse.json({ error: errorText }, { status: 403 });
      }
      throw new Error("대화 평가를 가져오는데 실패했습니다.");
    }
    const contentType = response.headers.get("content-type") || "";
    let data: string;
    if (contentType.includes("application/json")) {
      data = JSON.stringify(await response.json());
    } else {
      data = await response.text();
    }
    return NextResponse.json({ response: data });
  } catch (error: any) {
    console.error("Evaluation API error:", error);
    return NextResponse.json(
      { error: "Internal Server Error" },
      { status: 500 }
    );
  }
}
