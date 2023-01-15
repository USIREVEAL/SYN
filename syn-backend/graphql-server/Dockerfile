FROM openjdk:17-jdk-oracle
WORKDIR /syn
COPY build/libs/graphql-server-1.0-SNAPSHOT.jar /syn
RUN mkdir /syn/data
RUN mkdir /syn/projects
ENTRYPOINT ["java","-jar","graphql-server-1.0-SNAPSHOT.jar"]
