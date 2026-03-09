FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copy parent + module poms (dependency cache layer)
COPY pom.xml .
COPY api/pom.xml api/pom.xml
COPY business/pom.xml business/pom.xml
COPY commons/pom.xml commons/pom.xml
COPY persistence/pom.xml persistence/pom.xml

RUN mvn -B -DskipTests dependency:go-offline

# copy module sources
COPY api/src api/src
COPY business/src business/src
COPY commons/src commons/src
COPY persistence/src persistence/src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app


COPY --from=build /app/api/target/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]