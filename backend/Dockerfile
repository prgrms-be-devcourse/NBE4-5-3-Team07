FROM bellsoft/liberica-openjdk-alpine:21

# 필요한 패키지 설치 (tzdata 포함)
RUN apk add --no-cache tzdata

# Asia/Seoul로 타임존 설정
ENV TZ=Asia/Seoul


# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# data 디렉토리 통째로 복사
COPY data ./data

# 실행 명령어
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=prod"]
