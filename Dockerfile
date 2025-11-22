# Використовуємо офіційний образ Eclipse Temurin
FROM eclipse-temurin:21-jdk-jammy AS build

# Встановлюємо робочу директорію
WORKDIR /app

# 1. КОПІЮЄМО ВСІ POM.XML та mvnw
COPY pom.xml .
COPY tour-agency-common/pom.xml tour-agency-common/
COPY tour-agency-repo/pom.xml tour-agency-repo/
COPY tour-agency-security/pom.xml tour-agency-security/
COPY tour-agency-service/pom.xml tour-agency-service/
COPY tour-agency-web/pom.xml tour-agency-web/
COPY mvnw .
COPY .mvn .mvn

# FIX: Встановлюємо дозвіл на виконання
RUN chmod +x mvnw

# Завантажуємо всі залежності Maven
RUN ./mvnw dependency:go-offline

# 2. КОПІЮЄМО ВИХІДНИЙ КОД (Src files)
COPY tour-agency-common/src tour-agency-common/src
COPY tour-agency-repo/src tour-agency-repo/src
COPY tour-agency-security/src tour-agency-security/src
COPY tour-agency-service/src tour-agency-service/src
COPY tour-agency-web/src tour-agency-web/src

# 3. Виконуємо повну збірку
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Копіюємо зібраний JAR-файл
COPY --from=build tour-agency-web/target/tour-agency-web-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]