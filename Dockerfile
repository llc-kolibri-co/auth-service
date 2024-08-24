FROM gradle:latest as builder
USER root
WORKDIR /usr/src/java-code
COPY . /usr/src/java-code/

RUN gradle bootJar

FROM openjdk:17
#EXPOSE 8080
WORKDIR /usr/src/java-app
COPY --from=builder /usr/src/java-code/build/libs/*.jar ./auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]