# Використовуємо офіційний образ Java 21 (або ту версію, яку ви використовуєте)
FROM openjdk:21-jdk-slim AS build

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо кореневий pom.xml та pom.xml кожного модуля
COPY pom.xml .
COPY tour-agency-common/pom.xml tour-agency-common/
COPY tour-agency-repo/pom.xml tour-agency-repo/
COPY tour-agency-security/pom.xml tour-agency-security/
COPY tour-agency-service/pom.xml tour-agency-service/
COPY tour-agency-web/pom.xml tour-agency-web/

# Завантажуємо всі залежності Maven
RUN ./mvnw dependency:go-offline

# Копіюємо вихідний код
COPY tour-agency-common/src tour-agency-common/src
COPY tour-agency-repo/src tour-agency-repo/src
COPY tour-agency-security/src tour-agency-security/src
COPY tour-agency-service/src tour-agency-service/src
COPY tour-agency-web/src tour-agency-web/src

# Виконуємо повну збірку проєкту
RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо зібраний JAR-файл з папки target/web в кореневу папку образу
COPY --from=build tour-agency-web/target/tour-agency-web-1.0-SNAPSHOT.jar app.jar

# Встановлюємо порт
EXPOSE 8080

# Визначаємо команду запуску
ENTRYPOINT ["java", "-jar", "app.jar"]

# Виконуємо повну збірку проєкту
RUN mvn clean package -DskipTests
