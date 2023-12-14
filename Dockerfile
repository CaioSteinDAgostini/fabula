FROM openjdk:17-jdk-alpine
MAINTAINER caio
COPY target/fabula-0.0.1-SNAPSHOT.jar fabula-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/fabula-0.0.1-SNAPSHOT.jar"]
