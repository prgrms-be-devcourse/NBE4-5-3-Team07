# 1. Build Stage
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

# 캐시 최적화를 위해 의존성 먼저 복사
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle build --no-daemon || true

# 전체 프로젝트 복사 후 빌드
COPY . .
RUN ./gradlew build --no-daemon

# 2. Runtime Stage
FROM bellsoft/liberica-openjdk-alpine:21

# 빌드 결과물 복사 (Fat Jar)
COPY --from=builder /app/build/libs/*.jar app.jar

# 프로필 지정하여 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=prod"]
