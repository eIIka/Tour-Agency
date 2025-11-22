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

# FIX: Знаходимо зібраний JAR та переміщуємо його в корінь стадії
# Ми використовуємо COPY, щоб отримати файл, а потім перейменовуємо.
# Спочатку копіюємо JAR у відому тимчасову папку /tmp
RUN cp tour-agency-web/target/*.jar /tmp/app.jar

# ----------------------------------------------------
# STAGE 2: RUNTIME (Фінальний образ)
# ----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy

# Встановлюємо робочу директорію
WORKDIR /app

# FIX: Копіюємо файл з відомого шляху /tmp/app.jar
COPY --from=build /tmp/app.jar app.jar

EXPOSE 8080

# Визначаємо команду запуску
ENTRYPOINT ["java", "-jar", "app.jar"]