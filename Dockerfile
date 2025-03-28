FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /build
# Copier uniquement les fichiers nécessaires pour la résolution des dépendances
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
# Télécharger les dépendances (utilise le cache Docker si pom.xml n'a pas changé)
RUN ./mvnw dependency:go-offline

# Copier le code source et builder
COPY src ./src
RUN ./mvnw package -DskipTests \
    && java -Djarmode=layertools -jar target/*.jar extract

# Second stage: runtime
FROM eclipse-temurin:21-jdk-jammy as runtime
WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8082
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]