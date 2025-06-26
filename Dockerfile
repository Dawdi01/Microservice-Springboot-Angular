FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# ⬇️ Copier les modules
COPY ./user-api ./user-api
COPY ./gestion-materiel ./gestion-materiel

# 🔧 Installer user-api
RUN mvn -f user-api/pom.xml clean install -DskipTests

# 📦 Builder gestion-materiel
RUN mvn -f gestion-materiel/pom.xml clean package -DskipTests

# Étape 2 : Exécution
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/gestion-materiel/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
