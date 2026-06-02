FROM openjdk:21-ea-20-jdk
EXPOSE 8080
ARG JAR_FILE=server/build/libs/server-all.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]