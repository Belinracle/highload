FROM maven:alpine
ENV HOME=/usr/lib
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN --mount=type=cache,target=/root/.m2 mvn verify --fail-never
ADD ./src $HOME/src
RUN --mount=type=cache,target=/root/.m2 mvn install