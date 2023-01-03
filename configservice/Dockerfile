FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN --mount=type=cache,target=/root/.m2 mvn verify --fail-never
ADD ./src $HOME/src
RUN --mount=type=cache,target=/root/.m2 mvn package

FROM openjdk:11-jdk-slim
RUN apt-get update && apt-get install -y curl
COPY --from=build /usr/app/target/configservice-1.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar