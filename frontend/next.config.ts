// next.config.js

/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    // 빌드 시 ESLint 에러를 무시하여 배포할 수 있음 (권장되진 않음)
    ignoreDuringBuilds: true,
  },
  webpack(
    config: {
      devServer: {
        port: number; // 개발 서버의 포트
        liveReload: boolean;
        host: string; // 모든 네트워크 인터페이스에서 접속 허용
        allowedHosts: string; // 모든 호스트 허용
        open: boolean; // 서버 시작 시 브라우저 자동 열기
        client: {
          overlay: boolean; // 오류나 경고 시 화면에 오버레이 표시
          webSocketURL: string;
        };
        compress: boolean;
      };
    },
    { isServer }: any
  ) {
    if (!isServer) {
      config.devServer = {
        port: 3000, // 개발 서버의 포트
        liveReload: true,
        host: "0.0.0.0", // 모든 네트워크 인터페이스에서 접속 허용
        allowedHosts: "all", // 모든 호스트 허용
        open: true, // 서버 시작 시 브라우저 자동 열기
        client: {
          overlay: true, // 오류나 경고 시 화면에 오버레이 표시
          webSocketURL: process.env.NEXT_PUBLIC_WEBSOCKET_URL as string,
        },
        compress: true, // 압축 설정
      };
    }
    return config;
  },
};

module.exports = nextConfig;
