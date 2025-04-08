FROM bellsoft/liberica-openjdk-alpine:21


# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=prod"]
