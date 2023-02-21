FROM openjdk:17 as build

COPY . /tx-clearing-house-mimic/

WORKDIR /tx-clearing-house-mimic

RUN microdnf install dos2unix

RUN dos2unix mvnw
RUN chmod +x mvnw
RUN dos2unix .mvn/wrapper/maven-wrapper.properties

RUN ./mvnw clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM bellsoft/liberica-openjdk-alpine:17.0.4.1-1
RUN apk update && apk upgrade
ARG DEPENDENCY=/tx-clearing-house-mimic/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.dummy.clearing.house.ClearingHouseDummyApplication"]

EXPOSE 8080
