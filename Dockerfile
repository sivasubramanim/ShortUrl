FROM maven:3-jdk-8 AS build
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM openjdk:8
COPY --from=build /tmp/target/urlshortener.jar urlshortener.jar
#ADD /tmp/target/urlshortener.jar urlshortener.jar
RUN bash -c 'touch /urlshortener.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","/urlshortener.jar"]