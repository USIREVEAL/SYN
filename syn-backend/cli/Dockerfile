FROM openjdk:17.0.2-jdk

COPY  build/libs/cli-1.0-SNAPSHOT-all.jar /app/
CMD chmod 644 /app/cli-1.0-SNAPSHOT-all.jar
ENV SYN_HOME=/app/syn-data
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/cli-1.0-SNAPSHOT-all.jar"]