/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: true,
  },
  webpack(config, { isServer }) {
    if (!isServer) {
      config.devServer = {
        port: 3000,
        liveReload: true,
        host: "0.0.0.0",
        allowedHosts: "all",
        open: true,
        client: {
          overlay: true,
          webSocketURL: process.env.NEXT_PUBLIC_WEBSOCKET_URL,
        },
        compress: true,
      };
    }
    return config;
  },
};

module.exports = nextConfig;
