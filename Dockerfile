FROM amazoncorretto:11-alpine-jdk
MAINTAINER PTI
COPY target/pti-0.0.1-SNAPSHOT.jar pti-app.jar
ENTRYPOINT ["java","-jar","/pti-app.jar"]
