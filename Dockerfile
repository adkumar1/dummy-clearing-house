FROM maven:3.8.7-eclipse-temurin-17 AS build

COPY . /tx-clearing-house-mimic/

WORKDIR /tx-clearing-house-mimic



RUN mvn clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17.0.6_10-jdk-alpine

RUN apk update && apk upgrade
ARG DEPENDENCY=/tx-clearing-house-mimic/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.dummy.clearing.house.ClearingHouseDummyApplication"]

EXPOSE 8080
