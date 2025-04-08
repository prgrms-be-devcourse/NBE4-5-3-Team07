FROM bellsoft/liberica-openjdk-alpine:21


# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# data 디렉토리 통째로 복사
COPY data /app/data

# 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=prod"]
