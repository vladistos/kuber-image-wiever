FROM openjdk:17-jdk-alpine as build
ARG JAR_FILE=target
#VOLUME /tmp
#COPY build/libs/kuber-1.0.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/app.jar"]