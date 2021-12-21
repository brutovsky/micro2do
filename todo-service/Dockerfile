FROM openjdk:17-alpine
VOLUME /tmp
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ADD target/todo-service.jar /app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "/app/app.jar"]