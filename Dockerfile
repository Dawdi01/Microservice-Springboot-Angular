# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# ⬇️ Copier tout le projet (user-api + gestion-materiel)
COPY ./user-api ./user-api
COPY ./gestion-materiel ./gestion-materiel
COPY ./pom.xml . # uniquement si tu as un parent POM

# 🔧 Installer user-api pour que gestion-materiel puisse le référencer
RUN mvn -f user-api/pom.xml clean install -DskipTests

# 📦 Builder gestion-materiel
RUN mvn -f gestion-materiel/pom.xml clean package -DskipTests

# Étape 2 : Exécutable JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copier le JAR produit
COPY --from=build /app/gestion-materiel/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
