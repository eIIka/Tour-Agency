# ----------------------------------------------------
# STAGE 1: BUILD (Збірка додатку)
# ----------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS build

# Встановлюємо робочу директорію для першої стадії
WORKDIR /app

# 1. КОПІЮЄМО ВСІ POM.XML
COPY pom.xml .
COPY tour-agency-common/pom.xml tour-agency-common/
COPY tour-agency-repo/pom.xml tour-agency-repo/
COPY tour-agency-security/pom.xml tour-agency-security/
COPY tour-agency-service/pom.xml tour-agency-service/
COPY tour-agency-web/pom.xml tour-agency-web/

# Копіюємо Maven Wrapper та його конфігурацію
COPY mvnw .
COPY .mvn .mvn
# Встановлюємо дозвіл на виконання
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

# 4. FIX: Знаходимо зібраний JAR і переміщуємо його в /app/app.jar
# Це робить шлях до JAR-файлу абсолютним і надійним
RUN find tour-agency-web/target -name "*.jar" -print -quit -exec mv {} app.jar \;

# ----------------------------------------------------
# STAGE 2: RUNTIME (Фінальний образ)
# ----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо JAR-файл, який був переміщений у /app на стадії 'build'
COPY --from=build /app/app.jar app.jar

EXPOSE 8080

# Визначаємо команду запуску
ENTRYPOINT ["java", "-jar", "app.jar"]