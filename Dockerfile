FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/wallet-service-0.0.1-SNAPSHOT.jar wallet.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "wallet.jar"]