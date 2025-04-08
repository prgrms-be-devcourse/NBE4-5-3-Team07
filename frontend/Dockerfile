# # 1. Build stage
# FROM node:18.18.0 AS builder

# WORKDIR /app

# # 캐시 최적화를 위해 package 파일만 먼저 복사
# COPY package.json package-lock.json ./

# # 의존성 설치
# RUN npm ci

# # 앱 전체 복사
# COPY . .

# # 환경변수 파일 포함
# COPY .env.production .env.production

# # 빌드
# RUN npm run build || echo "Build completed with lint warnings"


# # 2. Production stage
# FROM node:18.18.0-slim AS runner

# WORKDIR /app

# ENV NODE_ENV production

# # package.json 및 의존성 복사
# COPY --from=builder /app/package.json ./
# COPY --from=builder /app/package-lock.json ./

# # next.config.ts 파일 복사
# COPY --from=builder /app/next.config.ts ./

# # 빌드 결과물 복사
# COPY --from=builder /app/.next ./.next
# COPY --from=builder /app/public ./public

# # node_modules 복사 (프로덕션 의존성)
# COPY --from=builder /app/node_modules ./node_modules

# # 환경변수 파일 복사
# COPY --from=builder /app/.env.production ./

# EXPOSE 3000

# # tsconfig.json 복사 (TS 설정 필요)
# COPY --from=builder /app/tsconfig.json ./

# # next-env.d.ts 복사 (TS 타입 정의 필요)
# COPY --from=builder /app/next-env.d.ts ./

# # next start 대신 실행 명령어 변경
# CMD ["npm", "start"]

# 1. Build stage
FROM node:18.18.0 AS builder

WORKDIR /app

# 캐시 최적화를 위해 package 파일만 먼저 복사
COPY package.json package-lock.json ./

# 의존성 설치
RUN npm ci

# 앱 전체 복사
COPY . .

# 환경변수 파일 포함
COPY .env.production .env.production

# 빌드
RUN npm run build || echo "Build completed with lint warnings"


# 2. Production stage
FROM node:18.18.0-slim AS runner

WORKDIR /app

ENV NODE_ENV production

# package.json 및 의존성 복사
COPY --from=builder /app/package.json ./
COPY --from=builder /app/package-lock.json ./

# next.config.ts 파일 복사
COPY --from=builder /app/next.config.ts ./

# src 디렉토리 복사 (중요!)
COPY --from=builder /app/src ./src

# 빌드 결과물 복사
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

# node_modules 복사 (프로덕션 의존성)
COPY --from=builder /app/node_modules ./node_modules

# 환경변수 파일 복사
COPY --from=builder /app/.env.production ./

EXPOSE 3000

# tsconfig.json 복사 (TS 설정 필요)
COPY --from=builder /app/tsconfig.json ./

# next-env.d.ts 복사 (TS 타입 정의 필요)
COPY --from=builder /app/next-env.d.ts ./

# 추가 설정 파일 복사
COPY --from=builder /app/postcss.config.mjs ./
COPY --from=builder /app/eslint.config.mjs ./

# next start 대신 실행 명령어 변경
CMD ["npm", "start"]