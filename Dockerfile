FROM maven:3.3.9-jdk-8

WORKDIR /WebService
COPY *.xml /WebService/
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

COPY src /WebService/src
RUN ["mvn", "package"]

EXPOSE 80
CMD ["java", "-jar", "target/WebService-1.0-jar-with-dependencies.jar"]

