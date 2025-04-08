import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  try {
    const cookie = req.headers.get("cookie");
    const body = await req.json();

    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/interview/evaluation`,
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

    const contentType = response.headers.get("content-type") || "";

    let data: string;
    if (contentType.includes("application/json")) {
      const json = await response.json();
      data = JSON.stringify(json);
    } else {
      const text = await response.text();
      data = text;
    }

    return NextResponse.json({ response: data });
  } catch (error: any) {
    return NextResponse.json(
      { error: "Internal Server Error", detail: error.message },
      { status: 500 }
    );
  }
}
