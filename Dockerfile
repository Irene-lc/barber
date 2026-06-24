# Etapa 1: compilar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY core/pom.xml core/
COPY data/pom.xml data/
COPY barber-api/pom.xml barber-api/
RUN mvn dependency:go-offline -B
COPY core/src core/src
COPY data/src data/src
COPY barber-api/src barber-api/src
RUN mvn clean package -DskipTests -B

# Etapa 2: imagen final liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/barber-api/target/barber-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]