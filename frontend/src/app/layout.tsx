import type { Metadata } from "next";
import "./globals.css";
import ClientLayout from "./ClientLayout";
import localFont from "next/font/local";
import { config } from "@fortawesome/fontawesome-svg-core";
import "@fortawesome/fontawesome-svg-core/styles.css";
import ChatWidget from "./chat/FlotingChat";

config.autoAddCss = false;

export const metadata: Metadata = {
  title: "DevPrep",
  description: "개발자의 꿈을 현실로",
};

const pretendard = localFont({
  src: "./../../node_modules/pretendard/dist/web/variable/woff2/PretendardVariable.woff2",
  display: "swap",
  weight: "45 920",
  variable: "--font-pretendard",
});

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={pretendard.variable}>
      <body className={`min-h-screen flex flex-col ${pretendard.className}`}>
        <ClientLayout>{children}</ClientLayout>
        <ChatWidget />
      </body>
    </html>
  );
}
