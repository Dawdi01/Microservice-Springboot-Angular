# Étape 1 : Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY gestion-materiel .

# 🔧 Installer user-api avant gestion-materiel
RUN mvn -f user-api/pom.xml clean install -DskipTests

# 📦 Builder gestion-materiel
RUN mvn -f gestion-materiel/pom.xml clean package -DskipTests

# Étape 2 : Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/gestion-materiel/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
