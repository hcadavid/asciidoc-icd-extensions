FROM openjdk:8-jre-alpine


COPY target/spring-boot-*.war /app.war
