# 1. JDK 21 베이스 이미지
FROM openjdk:21

# 2. 포트 8080 오픈
EXPOSE 8080

# 3. JAR 파일을 이미지에 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# 4. Spring Boot 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
