FROM openjdk:21-jdk-slim AS build

RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim

COPY --from=build /app/target/InventoryManagement-*.jar InventoryManagement.jar

ENTRYPOINT ["java", "-jar", "InventoryManagement.jar"]