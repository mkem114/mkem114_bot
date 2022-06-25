FROM maven:3-openjdk-17

COPY ./pom.xml /mkem114_bot/
COPY ./src /mkem114_bot/src/

WORKDIR /mkem114_bot/

RUN mvn clean install -DskipTests

CMD ["mvn", "exec:java"]
