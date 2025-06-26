# Étape 1 : Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copier tout le projet (user-api et gestion-materiel)
COPY . .

# Installer d'abord user-api pour éviter l'erreur de dépendance
RUN mvn -f user-api/pom.xml clean install -DskipTests

# Puis builder gestion-materiel
RUN mvn -f gestion-materiel/pom.xml clean package -DskipTests

# Étape 2 : Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copier uniquement le jar final
COPY --from=build /app/gestion-materiel/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
