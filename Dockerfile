# Використовуємо офіційний образ Eclipse Temurin
FROM eclipse-temurin:21-jdk-jammy AS build

# Встановлюємо робочу директорію
WORKDIR /app

# Завантажуємо всі залежності Maven
COPY pom.xml mvnw ./

# 2. КОПІЮЄМО ВИХІДНИЙ КОД (Src files)
COPY tour-agency-common /app/tour-agency-common
COPY tour-agency-repo /apptour-agency-repo
COPY tour-agency-security /apptour-agency-security
COPY tour-agency-service /apptour-agency-service
COPY tour-agency-web /apptour-agency-web

# Copy and prepare Maven wrapper
COPY .mvn /app/.mvn
RUN chmod +x mvnw

# Build the project and skip tests
RUN ./mvnw clean package -DskipTests

# ————————————————————————————————————
# Друга стадія: Фінальний образ
# ————————————————————————————————————
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# FIX: Копіюємо файл з кореня стадії 'build'
COPY --from=build /app/tour-agency-web/target/tour-agency-web-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]